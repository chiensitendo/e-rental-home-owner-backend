package com.e_rental.owner.controllers;

import com.e_rental.owner.entities.Users;
import com.e_rental.owner.responses.UserListResponse;
import com.e_rental.owner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<List<UserListResponse>> getAll(){
        return userService.getAll();
//        return ResponseEntity.ok(userService.getAll());
    }

}
