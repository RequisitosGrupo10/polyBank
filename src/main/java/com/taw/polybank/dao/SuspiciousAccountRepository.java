package com.taw.polybank.dao;

import com.taw.polybank.entity.SuspiciousAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuspiciousAccountRepository extends JpaRepository<SuspiciousAccountEntity, Integer> {

    Optional<SuspiciousAccountEntity> findByIban(String iban);

}
