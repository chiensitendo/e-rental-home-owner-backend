package com.e_rental.owner.services;

import com.e_rental.owner.dto.ErrorDto;
import com.e_rental.owner.dto.request.LoginRequest;
import com.e_rental.owner.entities.Users;
import com.e_rental.owner.repositories.UserRepository;
import com.e_rental.owner.responses.UserListResponse;
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
    private final UserRepository userRepository;
    private UserAuthenticationProvider userAuthenticationProvider;
    private AuthenticationManager authenticationManager;

    public ResponseEntity<List<UserListResponse>> getAll() {
        List<Users> userList = userRepository.findAll();
        List<UserListResponse> response = userList.stream().map(user -> {
            UserListResponse res = new UserListResponse();
            res.name = user.getUsername();
            return res;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Users> signUp(Users user) throws ErrorDto {
        if(!userRepository.existsByUsername(user.getUsername())){
            userRepository.save(user);
            return new ResponseEntity<Users>(user, HttpStatus.CREATED);
        }else {
            throw new ErrorDto("Tài khoản này đã tồn tại");
        }
    }


    public ResponseEntity<String> signIn(LoginRequest user) throws ErrorDto {
        try{
            Authentication auth =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(auth);
            String jwt = SecurityConstants.TOKEN_PREFIX + userAuthenticationProvider.createToken(user.getUserName());

            return ResponseEntity.ok(jwt);

        }catch (AuthenticationException e){
            throw new ErrorDto("Tên tài khoản hoặc Mật khẩu không đúng !");
        }
    }
}
