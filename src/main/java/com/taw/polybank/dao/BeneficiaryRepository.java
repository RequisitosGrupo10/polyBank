package com.taw.polybank.dao;

import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.BenficiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository extends JpaRepository<BenficiaryEntity, Integer> {

    BenficiaryEntity findBenficiaryEntityByNameAndIban(String name, String iban);
}
