package com.taw.polybank.dao;

import com.taw.polybank.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

    List<TransactionEntity> findTransactionEntitiesByClientByClientIdId(Integer id);
    List<TransactionEntity> findTransactionEntitiesByBankAccountByBankAccountIdId(Integer id);
}
