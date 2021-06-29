package com.e_rental.owner.repositories;

import com.e_rental.owner.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String email);

    boolean existsByUsername(String userNamel);
}
