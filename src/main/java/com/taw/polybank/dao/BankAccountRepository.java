package com.taw.polybank.dao;

import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.ClientEntity;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Integer> {

    List<BankAccountEntity> findByClientByClientId(@Param("client") ClientEntity client);

    Optional<BankAccountEntity> findByIban (String iban);

    BankAccountEntity findBankAccountEntityByIban(String iban);

    @Query("select distinct transaction.bankAccountByBankAccountId from TransactionEntity transaction where transaction.paymentByPaymentId in (select beneficiary.paymentsById from SuspiciousAccountEntity sus join BenficiaryEntity beneficiary on sus.iban = beneficiary.iban) and  transaction.bankAccountByBankAccountId.active = true ")
    List<BankAccountEntity> findSuspiciousTransactionAccount();

    @Query("select c from BankAccountEntity c " +
            "where NOT EXISTS (select t from TransactionEntity t where t.bankAccountByBankAccountId = c and t.timestamp > :timestamp)")
    List<BankAccountEntity> findInactiveAccountsFrom(Timestamp timestamp);

}
