package com.taw.polybank.service;

import com.taw.polybank.dao.TransactionRepository;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    protected TransactionRepository transactionRepository;


    public void save(TransactionDTO transactionDTO,
                     ClientService clientService, BankAccountService bankAccountService,
                     CurrencyExchangeService currencyExchangeService, PaymentService paymentService,
                     BadgeService badgeService, BeneficiaryService beneficiaryService) {
        TransactionEntity transaction = this.toEntity(transactionDTO, clientService, bankAccountService, currencyExchangeService, paymentService,badgeService, beneficiaryService);
        transactionRepository.save(transaction);
        transactionDTO.setId(transaction.getId());
    }

    private TransactionEntity toEntity(TransactionDTO transactionDTO,
                                       ClientService clientService, BankAccountService bankAccountService,
                                       CurrencyExchangeService currencyExchangeService, PaymentService paymentService,
                                       BadgeService badgeService, BeneficiaryService beneficiaryService) {
        TransactionEntity transaction = transactionRepository.findById(transactionDTO.getId()).orElse(new TransactionEntity());
        transaction.setId(transactionDTO.getId());
        transaction.setTimestamp(transactionDTO.getTimestamp());
        transaction.setClientByClientId(clientService.toEntidy(transactionDTO.getClientByClientId()));
        transaction.setBankAccountByBankAccountId(bankAccountService.toEntity(transactionDTO.getBankAccountByBankAccountId(), clientService, badgeService));
        transaction.setCurrencyExchangeByCurrencyExchangeId(currencyExchangeService.toEntity(transactionDTO.getCurrencyExchangeByCurrencyExchangeId(), badgeService));
        transaction.setPaymentByPaymentId(paymentService.toEntity(transactionDTO.getPaymentByPaymentId(), beneficiaryService, currencyExchangeService, badgeService));
        return transaction;
    }

    public List<TransactionDTO> findTransactionsByBankAccountByBankAccountIdId(int bankId) {
        return transactionRepository.findTransactionEntitiesByBankAccountByBankAccountIdId(bankId)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(int bankId, Timestamp dateAfter, Timestamp dateBefore, double minAmount, double maxAmount) {
        return transactionRepository.findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
                bankId, dateAfter, dateBefore, minAmount, maxAmount)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(int bankId, Timestamp dateAfter, Timestamp dateBefore, double minAmount, double maxAmount, String senderId) {
        return transactionRepository.findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
                bankId, dateAfter, dateBefore, minAmount, maxAmount,senderId)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(int bankId, Timestamp dateAfter, Timestamp dateBefore, double minAmount, double maxAmount, String recipientName) {
        return transactionRepository.findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
                bankId, dateAfter, dateBefore, minAmount, maxAmount,recipientName)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(int bankId, Timestamp dateAfter, Timestamp dateBefore, double minAmount, double maxAmount, String senderId, String recipientName) {
        return transactionRepository.findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
                bankId, dateAfter, dateBefore, minAmount, maxAmount, senderId, recipientName)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }
}
