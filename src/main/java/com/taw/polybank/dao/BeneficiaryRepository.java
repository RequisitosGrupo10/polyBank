package com.taw.polybank.dao;

import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.BenficiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<BenficiaryEntity, Integer> {

    public Optional<BenficiaryEntity> findByIban (String iban);

    BenficiaryEntity findBenficiaryEntityByNameAndIban(String name, String iban);
}
