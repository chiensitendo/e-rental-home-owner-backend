package com.e_rental.owner.controllers;

import com.e_rental.owner.dto.ErrorDto;
import com.e_rental.owner.dto.request.CreateBuildingRequest;
import com.e_rental.owner.dto.request.LoginRequest;
import com.e_rental.owner.dto.request.SignUpRequest;
import com.e_rental.owner.dto.request.UpdateOwnerRequest;
import com.e_rental.owner.dto.responses.OwnerResponse;
import com.e_rental.owner.dto.responses.LoginResponse;
import com.e_rental.owner.dto.responses.OwnerListResponse;
import com.e_rental.owner.dto.responses.Response;
import com.e_rental.owner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    @Autowired
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<OwnerListResponse>> getAll() {
        return userService.getAll();
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@Valid @RequestBody LoginRequest loginRequest) throws ErrorDto {
        return userService.signIn(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<OwnerResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) throws ErrorDto {
        return userService.signUp(signUpRequest);
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<Object> updateOwnerInfo(@PathVariable("ownerId") long ownerId, @Validated @RequestBody UpdateOwnerRequest updateOwnerRequest) throws Exception{
        return userService.updateOwnerInfo(ownerId, updateOwnerRequest);
    }

    @PostMapping("/{ownerId}/buildings")
    public ResponseEntity<Response> createBuilding(@PathVariable("ownerId") long ownerId, @Valid @RequestBody CreateBuildingRequest createBuildingRequest) throws ErrorDto, Exception {
        return userService.createBuilding(ownerId, createBuildingRequest);
    }

}
