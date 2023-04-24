package com.taw.polybank.dao;

import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Integer> {

    List<BankAccountEntity> findByClientByClientId(@Param("client") ClientEntity client);

    Optional<BankAccountEntity> findByIban (String iban);

    BankAccountEntity findBankAccountEntityByIban(String iban);

    //@Query("select payment.transactionsById[0].bankAccountByBankAccountId from SuspiciousAccountEntity sus join PaymentEntity payment on sus.iban = payment.benficiaryByBenficiaryId.iban ")
    //List<BankAccountEntity> findSuspiciousTransactionAccount();
}
