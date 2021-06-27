package com.e_rental.owner.repositories;

import com.e_rental.owner.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String email);

    boolean existsByUsername(String userNamel);
}
