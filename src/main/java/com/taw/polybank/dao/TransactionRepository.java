package com.taw.polybank.dao;

import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.BenficiaryEntity;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {
    List<TransactionEntity> findByBankAccountByBankAccountId(BankAccountEntity bankAccount);

    @Query("select t from TransactionEntity t where t.bankAccountByBankAccountId = :bankAccount and t.timestamp >= :begin and t.timestamp <= :end and t.clientByClientId in :clients and t.paymentByPaymentId.benficiaryByBenficiaryId = :beneficiary and t.paymentByPaymentId.amount >= :amount")
    List<TransactionEntity> filterByBankAccount_TimestampRange_TransactionOwner_beneficiaryIBAN_Amount(BankAccountEntity bankAccount, Timestamp begin, Timestamp end, List<ClientEntity> clients, BenficiaryEntity beneficiary, double amount);

    @Query("select t from TransactionEntity t where t.bankAccountByBankAccountId = :bankAccount and t.timestamp >= :begin and t.timestamp <= :end and t.paymentByPaymentId.benficiaryByBenficiaryId = :beneficiary and t.paymentByPaymentId.amount >= :amount")
    List<TransactionEntity> filterByBankAccount_TimestampRange_beneficiaryIBAN_Amount(BankAccountEntity bankAccount, Timestamp begin, Timestamp end, BenficiaryEntity beneficiary, double amount);

    @Query("select t from TransactionEntity t where t.bankAccountByBankAccountId = :bankAccount and t.timestamp >= :begin and t.timestamp <= :end and t.clientByClientId in :clients and t.paymentByPaymentId.amount >= :amount")
    List<TransactionEntity> filterByBankAccount_TimestampRange_TransactionOwner_Amount(BankAccountEntity bankAccount, Timestamp begin, Timestamp end, List<ClientEntity> clients, double amount);

    @Query("select t from TransactionEntity t where t.bankAccountByBankAccountId = :bankAccount and t.timestamp >= :begin and t.timestamp <= :end and t.paymentByPaymentId.amount >= :amount")
    List<TransactionEntity> filterByBankAccount_TimestampRange_Amount(BankAccountEntity bankAccount, Timestamp begin, Timestamp end, double amount);

    List<TransactionEntity> findTransactionEntitiesByClientByClientIdId(Integer id);

    List<TransactionEntity> findTransactionEntitiesByBankAccountByBankAccountIdId(Integer id);

    @Query("select trans from TransactionEntity trans where " +
            "trans.bankAccountByBankAccountId.id = :id " +
            "AND trans.timestamp >= :dateAfter " +
            "AND trans.timestamp <= :dateBefore " +
            "AND trans.paymentByPaymentId.amount >= :minAmount " +
            "AND trans.paymentByPaymentId.amount <= :maxAmount")
    List<TransactionEntity> findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
            @Param("id") Integer id,
            @Param("dateAfter") Timestamp dateAfter,
            @Param("dateBefore") Timestamp dateBefore,
            @Param("minAmount") double minAmount,
            @Param("maxAmount") double maxAmount);

    @Query("select trans from TransactionEntity trans where " +
            "trans.bankAccountByBankAccountId.id = :id " +
            "AND trans.timestamp >= :dateAfter " +
            "AND trans.timestamp <= :dateBefore " +
            "AND trans.paymentByPaymentId.amount >= :minAmount " +
            "AND trans.paymentByPaymentId.amount <= :maxAmount " +
            "AND lower(trans.clientByClientId.dni) = lower(:senderDni)")
    List<TransactionEntity> findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
            @Param("id") Integer id,
            @Param("dateAfter") Timestamp dateAfter,
            @Param("dateBefore") Timestamp dateBefore,
            @Param("minAmount") double minAmount,
            @Param("maxAmount") double maxAmount,
            @Param("senderDni") String senderDni);

    @Query("select trans from TransactionEntity trans where " +
            "trans.bankAccountByBankAccountId.id = :id " +
            "AND trans.timestamp >= :dateAfter " +
            "AND trans.timestamp <= :dateBefore " +
            "AND trans.paymentByPaymentId.amount >= :minAmount " +
            "AND trans.paymentByPaymentId.amount <= :maxAmount " +
            "AND lower(trans.paymentByPaymentId.benficiaryByBenficiaryId.name) LIKE lower(concat('%',:recipientName,'%')) ")
    List<TransactionEntity> findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
            @Param("id") Integer id,
            @Param("dateAfter") Timestamp dateAfter,
            @Param("dateBefore") Timestamp dateBefore,
            @Param("minAmount") double minAmount,
            @Param("maxAmount") double maxAmount,
            @Param("recipientName") String recipientName);

    @Query("select trans from TransactionEntity trans where " +
            "trans.bankAccountByBankAccountId.id = :id " +
            "AND trans.timestamp >= :dateAfter " +
            "AND trans.timestamp <= :dateBefore " +
            "AND trans.paymentByPaymentId.amount >= :minAmount " +
            "AND trans.paymentByPaymentId.amount <= :maxAmount " +
            "AND lower(trans.clientByClientId.dni) = lower(:senderDni) " +
            "AND lower(trans.paymentByPaymentId.benficiaryByBenficiaryId.name) LIKE lower(concat('%',:recipientName,'%')) ")
    List<TransactionEntity> findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
            @Param("id") Integer id,
            @Param("dateAfter") Timestamp dateAfter,
            @Param("dateBefore") Timestamp dateBefore,
            @Param("minAmount") double minAmount,
            @Param("maxAmount") double maxAmount,
            @Param("senderDni") String senderDni,
            @Param("recipientName") String recipientName);


    @Query("select transactionEntity from TransactionEntity transactionEntity where transactionEntity.clientByClientId.id = :id")
    List<TransactionEntity> findByClientId(int id);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join CurrencyExchangeEntity currency on (transactionEntity member of currency.transactionsById)" +
            "left join PaymentEntity payment on (transactionEntity member of payment.transactionsById)" +
            "where transactionEntity.clientByClientId.id = :id " +
            "and ((payment != null and payment.amount > :amount) " +
            "or (currency != null and currency.initialAmount > :amount))")
    List<TransactionEntity> findByClientIdAndMinimumAmount(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "where transactionEntity.clientByClientId.id = :id " +
            "and (transactionEntity.currencyExchangeByCurrencyExchangeId != null and transactionEntity.currencyExchangeByCurrencyExchangeId.initialAmount > :amount)")
    List<TransactionEntity> findCurrencyExchangesByClientIdAndMinimumAmount(Integer id, double amount);
    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "where transactionEntity.clientByClientId.id = :id " +
            "and (transactionEntity.paymentByPaymentId != null and transactionEntity.paymentByPaymentId.amount > :amount)")
    List<TransactionEntity> findPaymentsByClientIdAndMinimumAmount(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join CurrencyExchangeEntity currency on (transactionEntity member of currency.transactionsById)" +
            "left join PaymentEntity payment on (transactionEntity member of payment.transactionsById)" +
            "where transactionEntity.bankAccountByBankAccountId.id = :id " +
            "and ((payment != null and payment.amount > :amount) " +
            "or (currency != null and currency.initialAmount > :amount))")
    List<TransactionEntity> findByBankIdAndMinimumAmount(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "where transactionEntity.bankAccountByBankAccountId.id = :id " +
            "and (transactionEntity.currencyExchangeByCurrencyExchangeId != null and transactionEntity.currencyExchangeByCurrencyExchangeId.initialAmount > :amount)")
    List<TransactionEntity> findCurrencyExchangesByBankIdAndMinimumAmount(Integer id, double amount);
    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "where transactionEntity.bankAccountByBankAccountId.id = :id " +
            "and (transactionEntity.paymentByPaymentId != null and transactionEntity.paymentByPaymentId.amount > :amount)")
    List<TransactionEntity> findPaymentsByBankIdAndMinimumAmount(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "where transactionEntity.bankAccountByBankAccountId.id = :id ")
    List<TransactionEntity> findByBankAccountId(int id);


    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join PaymentEntity payment on (transactionEntity member of payment.transactionsById) " +
            "where transactionEntity.clientByClientId.id = :id " +
            "and (payment != null and payment.amount > :amount) " +
            "order by transactionEntity.timestamp desc ")
    List<TransactionEntity> findPaymentsByClientIdAndMinimumAmountSortByTimestampDesc(int id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join PaymentEntity payment on (transactionEntity member of payment.transactionsById) " +
            "where transactionEntity.clientByClientId.id = :id " +
            "and (payment != null and payment.amount > :amount) " +
            "order by transactionEntity.timestamp asc ")
    List<TransactionEntity> findPaymentsByClientIdAndMinimumAmountSortByTimestampAsc(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "where transactionEntity.bankAccountByBankAccountId.id = :id " +
            "order by transactionEntity.timestamp desc ")
    List<TransactionEntity> findByBankIdSortByTimestampDesc(Integer id);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "where transactionEntity.bankAccountByBankAccountId.id = :id " +
            "order by transactionEntity.timestamp asc ")
    List<TransactionEntity> findByBankIdSortByTimestampAsc(Integer id);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "where transactionEntity.clientByClientId.id = :id " +
            "order by transactionEntity.timestamp desc ")
    List<TransactionEntity> findByClientIdSortByTimestampDesc(Integer id);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "where transactionEntity.clientByClientId.id = :id " +
            "order by transactionEntity.timestamp asc ")
    List<TransactionEntity> findByClientIdSortByTimestampAsc(Integer id);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join CurrencyExchangeEntity currency on (transactionEntity member of currency.transactionsById)" +
            "where transactionEntity.bankAccountByBankAccountId.id = :id " +
            "and (currency != null and currency.initialAmount > :amount) " +
            "order by transactionEntity.timestamp asc ")
    List<TransactionEntity> findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampAsc(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join CurrencyExchangeEntity currency on (transactionEntity member of currency.transactionsById)" +
            "where transactionEntity.bankAccountByBankAccountId.id = :id " +
            "and (currency != null and currency.initialAmount > :amount) " +
            "order by transactionEntity.timestamp desc ")
    List<TransactionEntity> findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampDesc(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join PaymentEntity payment on (transactionEntity member of payment.transactionsById) " +
            "where transactionEntity.bankAccountByBankAccountId.id = :id " +
            "and (payment != null and payment.amount > :amount) " +
            "order by transactionEntity.timestamp asc ")
    List<TransactionEntity> findPaymentsByBankIdAndMinimumAmountSortByTimestampAsc(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join PaymentEntity payment on (transactionEntity member of payment.transactionsById) " +
            "where transactionEntity.bankAccountByBankAccountId.id = :id " +
            "and (payment != null and payment.amount > :amount) " +
            "order by transactionEntity.timestamp desc")
    List<TransactionEntity> findPaymentsByBankIdAndMinimumAmountSortByTimestampDesc(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join CurrencyExchangeEntity currency on (transactionEntity member of currency.transactionsById)" +
            "where transactionEntity.clientByClientId.id = :id " +
            "and (currency != null and currency.initialAmount > :amount) " +
            "order by transactionEntity.timestamp desc ")
    List<TransactionEntity> findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampDesc(Integer id, double amount);

    @Query("select transactionEntity from TransactionEntity transactionEntity " +
            "left join CurrencyExchangeEntity currency on (transactionEntity member of currency.transactionsById)" +
            "where transactionEntity.clientByClientId.id = :id " +
            "and (currency != null and currency.initialAmount > :amount) " +
            "order by transactionEntity.timestamp asc ")
    List<TransactionEntity> findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampAsc(Integer id, double amount);
}
