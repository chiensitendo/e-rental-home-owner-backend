package com.e_rental.owner.services;

import com.e_rental.owner.entities.Users;
import com.e_rental.owner.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public ResponseEntity<Object> getAll(){
        List<Users> userList = userRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(userList);
    }

}
