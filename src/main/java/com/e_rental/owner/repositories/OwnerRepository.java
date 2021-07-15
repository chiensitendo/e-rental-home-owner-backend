package com.e_rental.owner.repositories;

import com.e_rental.owner.entities.OwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<OwnerEntity, Long> {

    Optional<OwnerEntity> findById(Long id);

    Optional<OwnerEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<OwnerEntity> findByEmail(String email);

    Optional<OwnerEntity> findByUsernameOrEmail(String username, String email);
}
