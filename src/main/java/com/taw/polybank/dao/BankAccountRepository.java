package com.taw.polybank.dao;

import com.taw.polybank.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Integer> {

    BankAccountEntity findBankAccountEntityByIban(String iban);

    //@Query("select payment.transactionsById[0].bankAccountByBankAccountId from SuspiciousAccountEntity sus join PaymentEntity payment on sus.iban = payment.benficiaryByBenficiaryId.iban ")
    //List<BankAccountEntity> findSuspiciousTransactionAccount();
}
