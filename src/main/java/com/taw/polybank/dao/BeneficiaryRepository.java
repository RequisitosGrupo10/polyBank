package com.taw.polybank.dao;

import com.taw.polybank.entity.BenficiaryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Illya Rozumovskyy 50%
 * @author Lucía Gutiérrez Molina 50%
 */
@Repository
public interface BeneficiaryRepository extends JpaRepository<BenficiaryEntity, Integer> {

  Optional<BenficiaryEntity> findByIban(String iban);

  BenficiaryEntity findBenficiaryEntityByNameAndIban(String name, String iban);
}
