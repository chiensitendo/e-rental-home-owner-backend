package com.e_rental.owner.services;

import com.e_rental.owner.dto.ErrorDto;
import com.e_rental.owner.dto.request.LoginRequest;
import com.e_rental.owner.entities.Owner;
import com.e_rental.owner.enums.Role;
import com.e_rental.owner.enums.StatusCode;
import com.e_rental.owner.repositories.OwnerRepository;
import com.e_rental.owner.dto.responses.LoginResponse;
import com.e_rental.owner.dto.responses.UserListResponse;
import com.e_rental.owner.security.SecurityConstants;
import com.e_rental.owner.security.UserAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private final OwnerRepository ownerRepository;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public ResponseEntity<List<UserListResponse>> getAll() {
        List<Owner> ownerList = ownerRepository.findAll();
        List<UserListResponse> response = ownerList.stream().map(user -> {
            UserListResponse res = new UserListResponse();
            res.name = user.getUsername();
            return res;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<LoginResponse> signIn(LoginRequest user) throws ErrorDto {
        try {
            Authentication auth =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            String userName = user.getUsername();
            Owner dbOwner = ownerRepository.findByUsername(userName).get();
//            String token = SecurityConstants.TOKEN_PREFIX + userAuthenticationProvider.createToken(userName,
//                    dbUser.getRole());
            LoginResponse res= new LoginResponse();
            res.setCode(StatusCode.SUCCESS.getCode());
            res.setRole(Role.ROLE_OWNER);
            res.setToken(userAuthenticationProvider.createToken(userName,
                    Role.ROLE_OWNER));
            res.setTokenType(SecurityConstants.TOKEN_PREFIX.strip());
            res.setExpiredTime(SecurityConstants.EXPIRATION_TIME);
            return ResponseEntity.ok(res);

        } catch (AuthenticationException e) {
            throw new ErrorDto("Tên tài khoản hoặc Mật khẩu không đúng !");
        }
    }

    public ResponseEntity<Owner> signUp(Owner owner) throws ErrorDto {
        if (!ownerRepository.existsByUsername(owner.getUsername())) {
            ownerRepository.save(owner);
            return new ResponseEntity<Owner>(owner, HttpStatus.CREATED);
        } else {
            throw new ErrorDto("Tài khoản này đã tồn tại");
        }
    }
}
