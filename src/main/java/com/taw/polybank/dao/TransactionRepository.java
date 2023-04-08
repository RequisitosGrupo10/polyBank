package com.taw.polybank.dao;

import com.taw.polybank.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

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
}
