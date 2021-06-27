package com.e_rental.owner.controllers;

import com.e_rental.owner.dto.ErrorDto;
import com.e_rental.owner.dto.request.LoginRequest;
import com.e_rental.owner.entities.Users;
import com.e_rental.owner.responses.UserListResponse;
import com.e_rental.owner.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<List<UserListResponse>> getAll() {
        return userService.getAll();
    }

    @PostMapping("/user/signIn")
    public ResponseEntity<String> signIn(@RequestBody LoginRequest loginRequest) throws ErrorDto {
        System.out.println(loginRequest);
        return userService.signIn(loginRequest);
    }

    @PostMapping("/user/signUp")
    public ResponseEntity<Users> signUp(@RequestBody Map<String, Object> userInfo) throws ErrorDto {
        return userService.signUp(objectMapper.convertValue(userInfo, Users.class));
    }
}
