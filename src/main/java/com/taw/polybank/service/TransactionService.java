package com.taw.polybank.service;

import com.taw.polybank.controller.atm.TransactionFilterLucia;
import com.taw.polybank.dao.*;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.*;
import com.taw.polybank.dao.TransactionRepository;
import com.taw.polybank.entity.CurrencyExchangeEntity;
import com.taw.polybank.entity.TransactionEntity;
import com.taw.polybank.ui.transaction.TransactionFilterJose;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private CurrencyExchangeRepository currencyExchangeRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    public List<TransactionDTO> findByBankAccountByBankAccountId(BankAccountDTO bankAccount) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findByIban(bankAccount.getIban()).orElse(null);
        List<TransactionEntity> transactionEntities = transactionRepository.findByBankAccountByBankAccountId(bankAccountEntity);
        return entityListToDTO(transactionEntities);
    }

    public List<TransactionDTO> entityListToDTO (List<TransactionEntity> transactionEntityList){
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        for(TransactionEntity transactionEntity : transactionEntityList){
            transactionDTOList.add(transactionEntity.toDTO());
        }
        return transactionDTOList;
    }

    public List<TransactionDTO> filter(BankAccountDTO bankAccount, TransactionFilterLucia filter) {
        Timestamp begin = Timestamp.valueOf(filter.getTimestampBegin().toLocalDate().atTime(0,0));
        Timestamp end = Timestamp.valueOf(filter.getTimestampEnd().toLocalDate().atTime(23,59));

        List<ClientEntity> clients = clientRepository.findByNameOrSurname(filter.getTransactionOwner());
        BenficiaryEntity beneficiary = beneficiaryRepository.findByIban(filter.getBeneficiaryIban()).orElse(null);

        BankAccountEntity bankAccountEntity = bankAccountRepository.findByIban(bankAccount.getIban()).orElse(null);
        List<TransactionEntity> transactions;

        if(filter.getTransactionOwner().isBlank() || filter.getBeneficiaryIban().isBlank()){
            if(filter.getTransactionOwner().isBlank()){
                if(filter.getBeneficiaryIban().isBlank()){
                    transactions = transactionRepository.filterByBankAccount_TimestampRange_Amount(bankAccountEntity,begin,end, filter.getAmount());
                }else {
                    transactions = transactionRepository.filterByBankAccount_TimestampRange_beneficiaryIBAN_Amount(bankAccountEntity,begin,end,beneficiary, filter.getAmount());
                }
            }else{
                transactions = transactionRepository.filterByBankAccount_TimestampRange_TransactionOwner_Amount(bankAccountEntity,begin,end,clients, filter.getAmount());
            }
        }else{
            transactions = transactionRepository.filterByBankAccount_TimestampRange_TransactionOwner_beneficiaryIBAN_Amount(bankAccountEntity, begin, end, clients, beneficiary, filter.getAmount());
        }
        return entityListToDTO(transactions);
    }


    public void makeTransaction(double amount, String ibanReceptor, String nameReceptor, BadgeDTO finalBadgeDTO, BadgeDTO emisorBadgeDTO, BankAccountDTO bankAccountEmisorDTO, ClientDTO clientDTO) {

        BadgeEntity finalBadge = badgeRepository.findById(finalBadgeDTO.getId()).orElse(null);

        ClientEntity client = clientRepository.findByDNI(clientDTO.getDni());

        BenficiaryEntity beneficiary = getOrMakeBeneficiaryEntity(ibanReceptor, nameReceptor, finalBadge);

        //Make (if neccessary) a currency exchange
        CurrencyExchangeEntity currencyExchange = null;
        double finalAmount = amount;
        BadgeEntity emisorBadge = badgeRepository.findById(emisorBadgeDTO.getId()).orElse(null);
        if (emisorBadge.getId() != finalBadge.getId()) {
            //There exists a currency exchange
            currencyExchange = new CurrencyExchangeEntity();
            currencyExchange.setBadgeByFinalBadgeId(finalBadge);
            currencyExchange.setBadgeByInitialBadgeId(emisorBadge);
            currencyExchange.setInitialAmount(amount);
            finalAmount = amount * emisorBadge.getValue() / finalBadge.getValue();
            currencyExchange.setFinalAmount(finalAmount);

            currencyExchangeRepository.save(currencyExchange);
        }

        //Update the bank account(s)
        BankAccountEntity bankAccountEmisor = bankAccountRepository.findByIban(bankAccountEmisorDTO.getIban()).orElse(null);
        updateBankAccounts(amount, ibanReceptor, finalBadge, finalAmount, emisorBadge, bankAccountEmisor);


        //Make the payment
        PaymentEntity payment = makePayment(amount, beneficiary, currencyExchange);

        //Make the transaction
        makeTransaction(payment, client, bankAccountEmisor);

    }

    private void makeTransaction(PaymentEntity payment, ClientEntity client, BankAccountEntity bankAccount) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setBankAccountByBankAccountId(bankAccount);
        transaction.setClientByClientId(client);
        transaction.setPaymentByPaymentId(payment);
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        transactionRepository.save(transaction);
    }

    private PaymentEntity makePayment(double amount, BenficiaryEntity beneficiary, CurrencyExchangeEntity currencyExchange) {
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(amount);
        if (currencyExchange != null) {
            payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
        }
        payment.setBenficiaryByBenficiaryId(beneficiary);
        paymentRepository.save(payment);
        return payment;
    }

    private void updateBankAccounts(double amount, String ibanReceptor, BadgeEntity finalBadge, double finalAmount, BadgeEntity emisorBadge, BankAccountEntity bankAccountEmisor) {
        if (!ibanReceptor.equals(bankAccountEmisor.getIban())) {
            //Es una transferencia, se actualiza el dinero de ambas partes
            BankAccountEntity bankAccountReceptor = bankAccountRepository.findByIban(ibanReceptor).orElse(null);
            if (bankAccountReceptor != null) {
                bankAccountReceptor.setBalance(bankAccountReceptor.getBalance() + finalAmount);
                bankAccountRepository.save(bankAccountReceptor);
            }
            bankAccountEmisor.setBalance(bankAccountEmisor.getBalance() - amount);
        } else {
            //Está sacando dinero (ya sea de la misma o distinta moneda), por lo que hay que sacar el dinero final de la cuenta bancaria.
            bankAccountEmisor.setBalance(bankAccountEmisor.getBalance() - (amount * finalBadge.getValue() / emisorBadge.getValue()));
        }
        bankAccountRepository.save(bankAccountEmisor);
    }

    private BenficiaryEntity getOrMakeBeneficiaryEntity(String ibanReceptor, String nameReceptor, BadgeEntity finalBadge) {
        //Find or make the Beneficiary
        BenficiaryEntity beneficiary = beneficiaryRepository.findByIban(ibanReceptor).orElse(null);
        if (beneficiary == null) {
            beneficiary = new BenficiaryEntity();
            beneficiary.setName(nameReceptor);
            beneficiary.setIban(ibanReceptor);
            beneficiary.setBadge(finalBadge.getName());
            beneficiary.setSwift("");

            beneficiaryRepository.save(beneficiary);
        }
        return beneficiary;
    }

    public void save(TransactionDTO transactionDTO,
            ClientService clientService, BankAccountService bankAccountService,
            CurrencyExchangeService currencyExchangeService, PaymentService paymentService,
            BadgeService badgeService, BeneficiaryService beneficiaryService) {
        TransactionEntity transaction = this.toEntity(transactionDTO, clientService, bankAccountService,
                currencyExchangeService, paymentService, badgeService, beneficiaryService);
        transactionRepository.save(transaction);
        transactionDTO.setId(transaction.getId());
    }

    private TransactionEntity toEntity(TransactionDTO transactionDTO,
            ClientService clientService, BankAccountService bankAccountService,
            CurrencyExchangeService currencyExchangeService, PaymentService paymentService,
            BadgeService badgeService, BeneficiaryService beneficiaryService) {
        TransactionEntity transaction = transactionRepository.findById(transactionDTO.getId())
                .orElse(new TransactionEntity());
        transaction.setId(transactionDTO.getId());
        transaction.setTimestamp(transactionDTO.getTimestamp());
        transaction.setClientByClientId(clientService.toEntidy(transactionDTO.getClientByClientId()));
        transaction.setBankAccountByBankAccountId(bankAccountService
                .toEntity(transactionDTO.getBankAccountByBankAccountId(), clientService, badgeService));
        CurrencyExchangeEntity currencyExchangeEntity = transactionDTO.getCurrencyExchangeByCurrencyExchangeId() == null
                ? null
                : currencyExchangeService.toEntity(transactionDTO.getCurrencyExchangeByCurrencyExchangeId(),
                        badgeService);
        transaction.setCurrencyExchangeByCurrencyExchangeId(currencyExchangeEntity);
        transaction.setPaymentByPaymentId(paymentService.toEntity(transactionDTO.getPaymentByPaymentId(),
                beneficiaryService, currencyExchangeService, badgeService));
        return transaction;
    }

    public List<TransactionDTO> findTransactionsByBankAccountByBankAccountIdId(int bankId) {
        return transactionRepository.findTransactionEntitiesByBankAccountByBankAccountIdId(bankId)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(int bankId,
            Timestamp dateAfter, Timestamp dateBefore, double minAmount, double maxAmount) {
        return transactionRepository.findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
                bankId, dateAfter, dateBefore, minAmount, maxAmount)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
            int bankId, Timestamp dateAfter, Timestamp dateBefore, double minAmount, double maxAmount,
            String senderId) {
        return transactionRepository.findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
                bankId, dateAfter, dateBefore, minAmount, maxAmount, senderId)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
            int bankId, Timestamp dateAfter, Timestamp dateBefore, double minAmount, double maxAmount,
            String recipientName) {
        return transactionRepository.findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
                bankId, dateAfter, dateBefore, minAmount, maxAmount, recipientName)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
            int bankId, Timestamp dateAfter, Timestamp dateBefore, double minAmount, double maxAmount, String senderId,
            String recipientName) {
        return transactionRepository
                .findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
                        bankId, dateAfter, dateBefore, minAmount, maxAmount, senderId, recipientName)
                .stream()
                .map(transaction -> transaction.toDTO())
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findByClientIdAndFilter(Integer id, TransactionFilterJose filter) {
        if (filter.getType() == null || filter.getType().isBlank())
            return findByClientIdAndMinimumAmount(id, filter.getAmount());
        if (filter.getType().equals("Payment")) {
            return getPaymentByClientIdDTOS(id, filter);
        } else if (filter.getType().equals("CurrencyExchange")) {
            return getCurrencyExchangeByClientIdDTOS(id, filter);
        } else {
            return getAnyTransactionByClientIdDTOS(id, filter);
        }
    }

    public List<TransactionDTO> findByBankIdAndFilter(int id, TransactionFilterJose filter) {
        if (filter.getType() == null || filter.getType().isBlank())
            return getDtoList(transactionRepository.findByBankIdAndMinimumAmount(id, filter.getAmount()));
        if (filter.getType().equals("Payment")) {
            return getPaymentByBankIdDTOS(id, filter);
        } else if (filter.getType().equals("CurrencyExchange")) {
            return getCurrencyExchangeByBankIdDTOS(id, filter);
        } else
            return getAnyTransactionByClientIdDTOS(id, filter);
    }

    private List<TransactionDTO> getAnyTransactionByClientIdDTOS(Integer id, TransactionFilterJose filter) {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findByClientId(id);
        } else if (filter.getSorting().equals("timestamp_asc")) {
            return findByClientIdSortByTimestampAsc(id);
        } else if (filter.getSorting().equals("timestamp_desc")) {
            return findByClientIdSortByTimestampDesc(id);
        } else
            return findByClientId(id);
    }

    private List<TransactionDTO> getCurrencyExchangeByClientIdDTOS(Integer id, TransactionFilterJose filter) {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findCurrencyExchangesByClientIdAndMinimumAmount(id, filter.getAmount());
        } else if (filter.getSorting().equals("timestamp_asc")) {
            return findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampAsc(id, filter.getAmount());
        } else if (filter.getSorting().equals("timestamp_desc")) {
            return findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampDesc(id, filter.getAmount());
        } else
            return findCurrencyExchangesByClientIdAndMinimumAmount(id, filter.getAmount());
    }

    private List<TransactionDTO> getPaymentByClientIdDTOS(Integer id, TransactionFilterJose filter) {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findPaymentsByClientIdAndMinimumAmount(id, filter.getAmount());
        } else if (filter.getSorting().equals("timestamp_asc")) {
            return findPaymentsByClientIdAndMinimumAmountSortByTimestampAsc(id, filter.getAmount());
        } else if (filter.getSorting().equals("timestamp_desc")) {
            return findPaymentsByClientIdAndMinimumAmountSortByTimestampDesc(id, filter.getAmount());
        } else
            return findPaymentsByClientIdAndMinimumAmount(id, filter.getAmount());
    }

    private List<TransactionDTO> getAnyTransactionByBankIdDTOS(Integer id, TransactionFilterJose filter) {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findByBankId(id);
        } else if (filter.getSorting().equals("timestamp_asc")) {
            return findByBankIdSortByTimestampAsc(id);
        } else if (filter.getSorting().equals("timestamp_desc")) {
            return findByBankIdSortByTimestampDesc(id);
        } else
            return findByBankId(id);
    }

    private List<TransactionDTO> findByBankIdSortByTimestampDesc(Integer id) {
        return getDtoList(transactionRepository.findByBankIdSortByTimestampDesc(id));
    }

    private List<TransactionDTO> findByBankIdSortByTimestampAsc(Integer id) {
        return getDtoList(transactionRepository.findByBankIdSortByTimestampAsc(id));
    }

    private List<TransactionDTO> getCurrencyExchangeByBankIdDTOS(Integer id, TransactionFilterJose filter) {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findCurrencyExchangesByBankIdAndMinimumAmount(id, filter.getAmount());
        } else if (filter.getSorting().equals("timestamp_asc")) {
            return findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampAsc(id, filter.getAmount());
        } else if (filter.getSorting().equals("timestamp_desc")) {
            return findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampDesc(id, filter.getAmount());
        } else
            return findCurrencyExchangesByBankIdAndMinimumAmount(id, filter.getAmount());
    }

    private List<TransactionDTO> findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampAsc(Integer id,
            double amount) {
        return getDtoList(
                transactionRepository.findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampAsc(id, amount));
    }

    private List<TransactionDTO> findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampDesc(Integer id,
            double amount) {
        return getDtoList(
                transactionRepository.findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampDesc(id, amount));
    }

    private List<TransactionDTO> findCurrencyExchangesByBankIdAndMinimumAmount(Integer id, double amount) {
        return getDtoList(transactionRepository.findCurrencyExchangesByBankIdAndMinimumAmount(id, amount));
    }

    private List<TransactionDTO> getPaymentByBankIdDTOS(Integer id, TransactionFilterJose filter) {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findPaymentsByBankIdAndMinimumAmount(id, filter.getAmount());
        } else if (filter.getSorting().equals("timestamp_asc")) {
            return findPaymentsByBankIdAndMinimumAmountSortByTimestampAsc(id, filter.getAmount());
        } else if (filter.getSorting().equals("timestamp_desc")) {
            return findPaymentsByBankIdAndMinimumAmountSortByTimestampDesc(id, filter.getAmount());
        } else
            return findPaymentsByBankIdAndMinimumAmount(id, filter.getAmount());
    }

    private List<TransactionDTO> findPaymentsByBankIdAndMinimumAmountSortByTimestampAsc(Integer id, double amount) {
        return getDtoList(transactionRepository.findPaymentsByBankIdAndMinimumAmountSortByTimestampAsc(id, amount));
    }

    private List<TransactionDTO> findPaymentsByBankIdAndMinimumAmountSortByTimestampDesc(Integer id, double amount) {
        return getDtoList(transactionRepository.findPaymentsByBankIdAndMinimumAmountSortByTimestampDesc(id, amount));
    }

    private List<TransactionDTO> findPaymentsByBankIdAndMinimumAmount(Integer id, double amount) {
        return getDtoList(transactionRepository.findPaymentsByBankIdAndMinimumAmount(id, amount));
    }

    private List<TransactionDTO> findByClientIdSortByTimestampDesc(Integer id) {
        return getDtoList(transactionRepository.findByClientIdSortByTimestampDesc(id));
    }

    private List<TransactionDTO> findByClientIdSortByTimestampAsc(Integer id) {
        return getDtoList(transactionRepository.findByClientIdSortByTimestampAsc(id));
    }

    private List<TransactionDTO> findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampDesc(Integer id,
            double amount) {
        return getDtoList(
                transactionRepository.findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampDesc(id, amount));
    }

    private List<TransactionDTO> findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampAsc(Integer id,
            double amount) {
        return getDtoList(
                transactionRepository.findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampAsc(id, amount));
    }

    private List<TransactionDTO> findPaymentsByClientIdAndMinimumAmountSortByTimestampAsc(Integer id, double amount) {
        return getDtoList(transactionRepository.findPaymentsByClientIdAndMinimumAmountSortByTimestampAsc(id, amount));
    }

    public List<TransactionDTO> findByClientId(int id) {
        return getDtoList(transactionRepository.findByClientId(id));
    }

    private List<TransactionDTO> findCurrencyExchangesByClientIdAndMinimumAmount(Integer id, double amount) {
        return getDtoList(transactionRepository.findCurrencyExchangesByClientIdAndMinimumAmount(id, amount));
    }

    private List<TransactionDTO> findPaymentsByClientIdAndMinimumAmount(Integer id, double amount) {
        return getDtoList(transactionRepository.findPaymentsByClientIdAndMinimumAmount(id, amount));

    }

    public List<TransactionDTO> findByClientIdAndMinimumAmount(Integer id, double amount) {
        return getDtoList(transactionRepository.findByClientIdAndMinimumAmount(id, amount));
    }

    private List<TransactionDTO> getDtoList(List<TransactionEntity> transactionEntityList) {
        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        for (TransactionEntity transactionEntity : transactionEntityList) {
            transactionDTOS.add(transactionEntity.toDTO());
        }
        return transactionDTOS;
    }

    private List<TransactionDTO> findPaymentsByClientIdAndMinimumAmountSortByTimestampDesc(int id, double amount) {
        return getDtoList(transactionRepository.findPaymentsByClientIdAndMinimumAmountSortByTimestampDesc(id, amount));
    }

    public List<TransactionDTO> findByBankId(int id) {
        return getDtoList(transactionRepository.findByBankAccountId(id));
    }

    public BankAccountDTO updateBankAccount(BankAccountDTO bankAccountEmisor) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findByIban(bankAccountEmisor.getIban()).orElse(null);
        return bankAccountEntity == null ? null : bankAccountEntity.toDTO();
    }
}
