package com.e_rental.owner.controllers;

import com.e_rental.owner.entities.Users;
import com.e_rental.owner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Object> getAll(){
        return userService.getAll();
    }

}
