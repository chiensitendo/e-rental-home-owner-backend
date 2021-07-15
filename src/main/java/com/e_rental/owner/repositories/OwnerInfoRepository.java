package com.e_rental.owner.repositories;

import com.e_rental.owner.entities.OwnerInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerInfoRepository extends JpaRepository<OwnerInfoEntity, Long> {
}
