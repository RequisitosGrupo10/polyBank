package com.taw.polybank.dao;

import com.taw.polybank.entity.SuspiciousAccountEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Lucía Gutiérrez Molina
 */
@Repository
public interface SuspiciousAccountRepository
    extends JpaRepository<SuspiciousAccountEntity, Integer> {

  Optional<SuspiciousAccountEntity> findByIban(String iban);
}
