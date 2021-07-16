package com.e_rental.owner.services;

import com.e_rental.owner.dto.ErrorDto;
import com.e_rental.owner.dto.request.LoginRequest;
import com.e_rental.owner.dto.request.SignUpRequest;
import com.e_rental.owner.dto.request.UpdateOwnerRequest;
import com.e_rental.owner.dto.responses.OwnerResponse;
import com.e_rental.owner.dto.responses.OwnerInfoResponse;
import com.e_rental.owner.entities.Owner;
import com.e_rental.owner.entities.OwnerInfo;
import com.e_rental.owner.enums.Role;
import com.e_rental.owner.enums.StatusCode;
import com.e_rental.owner.handling.InternationalErrorException;
import com.e_rental.owner.handling.ResourceNotFoundException;
import com.e_rental.owner.mappers.OwnerInfoMapper;
import com.e_rental.owner.repositories.OwnerInfoRepository;
import com.e_rental.owner.repositories.OwnerRepository;
import com.e_rental.owner.dto.responses.LoginResponse;
import com.e_rental.owner.dto.responses.OwnerListResponse;
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

    @Autowired
    private OwnerInfoMapper ownerInfoMapper;

    public ResponseEntity<List<OwnerListResponse>> getAll() {
        List<Owner> ownerList = ownerRepository.findAll();
        List<OwnerListResponse> response = ownerList.stream().map(user -> {
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

        Owner owner = objectMapper.convertValue(signUpRequest, Owner.class);
        owner.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Create ownerInfo
        OwnerInfo ownerInfo = new OwnerInfo();
        ownerInfo.setGender(signUpRequest.getGender());
        ownerInfo.setFirstName(signUpRequest.getFirstName());
        ownerInfo.setLastName(signUpRequest.getLastName());
        ownerInfo.setAddress(signUpRequest.getAddress());
        ownerInfo.setProvinceId(signUpRequest.getProvinceId());

        owner.setInfo(ownerInfo);
        owner.setHasInfo(true);
        ownerInfo.setOwner(owner);
        ownerRepository.save(owner);

        OwnerResponse ownerResponse = new OwnerResponse();
        ownerResponse.setUsername(owner.getUsername());
        ownerResponse.setPassword(owner.getPassword());
        ownerResponse.setEmail(owner.getEmail());
        ownerResponse.setFirstName(ownerInfo.getFirstName());
        ownerResponse.setLastName(ownerInfo.getLastName());
        ownerResponse.setGender(ownerInfo.getGender());
        ownerResponse.setProvinceId(ownerInfo.getProvinceId());
        ownerResponse.setAddress(ownerInfo.getAddress());

        return new ResponseEntity<OwnerResponse>(ownerResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> updateOwnerInfo(long id, UpdateOwnerRequest updateOwnerRequest) throws Exception {
        try {
            Owner owner = Optional.of(ownerRepository.getById(id))
                    .orElseThrow();
            Long infoId = null;
            if (owner.getHasInfo() == false) {
                // create new info
                OwnerInfo ownerInfo = ownerInfoMapper.toOwnerInfo(updateOwnerRequest);
                owner.setInfo(ownerInfo);
                ownerInfo.setOwner(owner);
                ownerRepository.save(owner);
                infoId = ownerInfo.getId();
            } else {
                // update existing info
                OwnerInfo ownerInfo = owner.getInfo();
                ownerInfoMapper.updateOwnerInfo(updateOwnerRequest, ownerInfo);
                owner.setInfo(ownerInfo);
                ownerRepository.save(owner);
                infoId = ownerInfo.getId();
            }
            OwnerInfoResponse res = new OwnerInfoResponse();
            res.setCode(StatusCode.SUCCESS.getCode());
            res.setMessage(messageSourceUtil.getMessage("account.info.update.success"));
            if(updateOwnerRequest != null){
                res.setGender(updateOwnerRequest.getGender());
                res.setAddress(updateOwnerRequest.getAddress());
                res.setOwnerId(owner.getId());
                res.setId(infoId);
                res.setProvinceId(updateOwnerRequest.getProvinceId());
                res.setFirstName(updateOwnerRequest.getFirstName());
                res.setLastName(updateOwnerRequest.getLastName());
            }
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (EntityNotFoundException ee) {
            throw new ResourceNotFoundException(messageSourceUtil.getMessage("account.notExist"));
        } catch (Exception e){
            e.printStackTrace();
            throw new InternationalErrorException(messageSourceUtil.getMessage("error.server"));
        }
    }

    public ResponseEntity<OwnerInfoResponse> getOwnerInfo(long id) throws Exception {
        try {
            OwnerInfoResponse res = new OwnerInfoResponse();
            Owner owner = Optional.of(ownerRepository.getById(id))
                    .orElseThrow();
            if (owner.getHasInfo() == false) {
                res.setHasInfo(false);
            } else {
                OwnerInfo ownerInfo = owner.getInfo();
                res = ownerInfoMapper.toOwnerInfoResponse(ownerInfo);
                res.setHasInfo(true);
            }
            res.setCode(StatusCode.SUCCESS.getCode());

            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (EntityNotFoundException ee) {
            throw new ResourceNotFoundException(messageSourceUtil.getMessage("account.notExist"));
        } catch (Exception e){
            e.printStackTrace();
            throw new InternationalErrorException(messageSourceUtil.getMessage("error.server"));
        }
    }
}
