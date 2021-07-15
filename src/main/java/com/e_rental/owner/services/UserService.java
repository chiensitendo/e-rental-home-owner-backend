package com.e_rental.owner.services;

import com.e_rental.owner.dto.ErrorDto;
import com.e_rental.owner.dto.request.CreateBuildingRequest;
import com.e_rental.owner.dto.request.LoginRequest;
import com.e_rental.owner.dto.request.SignUpRequest;
import com.e_rental.owner.dto.request.UpdateOwnerRequest;
import com.e_rental.owner.dto.responses.*;
import com.e_rental.owner.entities.BuildingEntity;
import com.e_rental.owner.entities.OwnerEntity;
import com.e_rental.owner.entities.OwnerInfoEntity;
import com.e_rental.owner.enums.Role;
import com.e_rental.owner.enums.StatusCode;
import com.e_rental.owner.handling.InternationalErrorException;
import com.e_rental.owner.handling.ResourceNotFoundException;
import com.e_rental.owner.mappers.BuildingMapper;
import com.e_rental.owner.mappers.OwnerInfoMapper;
import com.e_rental.owner.repositories.BuildingRepository;
import com.e_rental.owner.repositories.OwnerInfoRepository;
import com.e_rental.owner.repositories.OwnerRepository;
import com.e_rental.owner.security.SecurityConstants;
import com.e_rental.owner.security.UserAuthenticationProvider;
import com.e_rental.owner.security.UserPrincipal;
import com.e_rental.owner.utils.MessageSourceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private OwnerInfoRepository ownerInfoRepository;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSourceUtil messageSourceUtil;

    private OwnerInfoMapper ownerInfoMapper;

    public ResponseEntity<List<OwnerListResponse>> getAll() {
        List<OwnerEntity> ownerEntityList = ownerRepository.findAll();
        List<OwnerListResponse> response = ownerEntityList.stream().map(user -> {
            OwnerListResponse res = new OwnerListResponse();
            res.name = user.getUsername();
            return res;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<LoginResponse> signIn(LoginRequest loginRequest) throws ErrorDto {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLoginId(),
                            loginRequest.getPassword()
                    )
            );

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginResponse res = new LoginResponse();
            res.setId(userPrincipal.getId());
            res.setCode(StatusCode.SUCCESS.getCode());
            res.setRole(Role.ROLE_OWNER);
            res.setToken(userAuthenticationProvider.createToken(userPrincipal));
            res.setTokenType(SecurityConstants.TOKEN_PREFIX.strip());
            res.setExpiredTime(SecurityConstants.EXPIRATION_TIME);

            return ResponseEntity.ok(res);

        } catch (AuthenticationException e) {
            throw new ErrorDto(messageSourceUtil.getMessage("account.error"));
        }
    }

    @Transactional
    public ResponseEntity<OwnerResponse> signUp(SignUpRequest signUpRequest) throws ErrorDto {

        //TODO: Add validate signUpRequest

        if (ownerRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new ErrorDto(messageSourceUtil.getMessage("account.exist"));
        }

        OwnerEntity ownerEntity = objectMapper.convertValue(signUpRequest, OwnerEntity.class);
        ownerEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Create ownerInfo
        OwnerInfoEntity ownerInfoEntity = new OwnerInfoEntity();
        ownerInfoEntity.setGender(signUpRequest.getGender());
        ownerInfoEntity.setFirstName(signUpRequest.getFirstName());
        ownerInfoEntity.setLastName(signUpRequest.getLastName());
        ownerInfoEntity.setAddress(signUpRequest.getAddress());
        ownerInfoEntity.setProvinceId(signUpRequest.getProvinceId());

        ownerEntity.setInfo(ownerInfoEntity);
        ownerEntity.setHasInfo(true);
        ownerInfoEntity.setOwner(ownerEntity);
        ownerRepository.save(ownerEntity);

        OwnerResponse ownerResponse = new OwnerResponse();
        ownerResponse.setUsername(ownerEntity.getUsername());
        ownerResponse.setPassword(ownerEntity.getPassword());
        ownerResponse.setEmail(ownerEntity.getEmail());
        ownerResponse.setFirstName(ownerInfoEntity.getFirstName());
        ownerResponse.setLastName(ownerInfoEntity.getLastName());
        ownerResponse.setGender(ownerInfoEntity.getGender());
        ownerResponse.setProvinceId(ownerInfoEntity.getProvinceId());
        ownerResponse.setAddress(ownerInfoEntity.getAddress());

        return new ResponseEntity<OwnerResponse>(ownerResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> updateOwnerInfo(long ownerId, UpdateOwnerRequest updateOwnerRequest) throws Exception {
        try {
            OwnerEntity ownerEntity = ownerRepository.findById(ownerId).orElseThrow(ResourceNotFoundException::new);

            Long infoId = null;
            if (ownerEntity.getHasInfo() == false) {
                // create new info
                OwnerInfoEntity ownerInfoEntity = ownerInfoMapper.toOwnerInfo(updateOwnerRequest);
                ownerEntity.setInfo(ownerInfoEntity);
                ownerInfoEntity.setOwner(ownerEntity);
                ownerRepository.save(ownerEntity);
                infoId = ownerInfoEntity.getId();
            } else {
                // update existing info
                OwnerInfoEntity ownerInfoEntity = ownerEntity.getInfo();
                ownerInfoMapper.updateOwnerInfo(updateOwnerRequest, ownerInfoEntity);
                ownerEntity.setInfo(ownerInfoEntity);
                ownerRepository.save(ownerEntity);
                infoId = ownerInfoEntity.getId();
            }
            OwnerInfoResponse res = new OwnerInfoResponse();
            res.setCode(StatusCode.SUCCESS.getCode());
            res.setMessage(messageSourceUtil.getMessage("account.info.update.success"));
            if(updateOwnerRequest != null){
                res.setGender(updateOwnerRequest.getGender());
                res.setAddress(updateOwnerRequest.getAddress());
                res.setOwnerId(ownerEntity.getId());
                res.setId(infoId);
                res.setProvinceId(updateOwnerRequest.getProvinceId());
                res.setFirstName(updateOwnerRequest.getFirstName());
                res.setLastName(updateOwnerRequest.getLastName());
            }
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (EntityNotFoundException ee) {
            throw new ResourceNotFoundException(String.format(messageSourceUtil.getMessage("resource.notExist"), "Owner"));
        } catch (Exception e){
            e.printStackTrace();
            throw new InternationalErrorException(messageSourceUtil.getMessage("error.server"));
        }
    }

    public ResponseEntity<Response> createBuilding(Long ownerId, CreateBuildingRequest createBuildingRequest) throws Exception {
        try {
            OwnerEntity ownerEntity = ownerRepository.findById(ownerId).orElseThrow(ResourceNotFoundException::new);

            BuildingEntity buildingEntity = BuildingMapper.INSTANCE.toBuildingEntity(createBuildingRequest);
            buildingEntity.setOwner(ownerEntity);

            buildingRepository.save(buildingEntity);
            return ResponseEntity.status(HttpStatus.OK).body(new Response());
        } catch (ResourceNotFoundException ee) {
            throw new ResourceNotFoundException(String.format(messageSourceUtil.getMessage("resource.notExist"), "Owner"));
        } catch (Exception e){
            e.printStackTrace();
            throw new InternationalErrorException(messageSourceUtil.getMessage("error.server"));
        }
    }
}
