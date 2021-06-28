package com.e_rental.owner.services;

import com.e_rental.owner.entities.User;
import com.e_rental.owner.repositories.UserRepository;
import com.e_rental.owner.responses.UserListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public ResponseEntity<List<UserListResponse>> getAll(){
        List<User> userList = userRepository.findAll();
        List<UserListResponse> response = userList.stream().map(user -> {
            UserListResponse res = new UserListResponse();
            res.name = user.getUsername();
            return res;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

}
