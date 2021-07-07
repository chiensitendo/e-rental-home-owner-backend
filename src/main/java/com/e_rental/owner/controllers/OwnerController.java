package com.e_rental.owner.controllers;

import com.e_rental.owner.dto.ErrorDto;
import com.e_rental.owner.dto.request.LoginRequest;
import com.e_rental.owner.dto.request.SignUpRequest;
import com.e_rental.owner.entities.Owner;
import com.e_rental.owner.dto.responses.LoginResponse;
import com.e_rental.owner.dto.responses.UserListResponse;
import com.e_rental.owner.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    @Autowired
    private final UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping()
    @ResponseBody
    public ResponseEntity<List<UserListResponse>> getAll() {
        return userService.getAll();
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@Valid @RequestBody LoginRequest loginRequest) throws ErrorDto {
        System.out.println(loginRequest);
        return userService.signIn(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<Owner> signUp(@Valid @RequestBody SignUpRequest signUpRequest) throws ErrorDto {
        System.out.println(signUpRequest);
        return userService.signUp(objectMapper.convertValue(signUpRequest, Owner.class));
    }
}
