package com.taw.polybank.service;

import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.BankAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankAccountService {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    protected ClientService clientService;

    @Autowired
    protected BadgeService badgeService;

    public List<BankAccountDTO> findAll(){
        List <BankAccountEntity> bankAccountEntityList = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = getDtoList(bankAccountEntityList);
        return bankAccountDTOS;
    }

    public List<BankAccountDTO> findSuspicious(){
        List <BankAccountEntity> bankAccountEntityList = bankAccountRepository.findSuspiciousTransactionAccount();
        List<BankAccountDTO> bankAccountDTOS = getDtoList(bankAccountEntityList);
        return bankAccountDTOS;
    }

    public List<BankAccountDTO> findInactive() {
        Timestamp timestamp = Timestamp.from(Instant.now());
        LocalDateTime dateTime = LocalDateTime.now().minusMonths(1);
        List<BankAccountEntity> bankAccountEntityList = bankAccountRepository.findInactiveAccountsFrom(dateTime);
        List<BankAccountDTO> bankAccountDTOS = getDtoList(bankAccountEntityList);
        return bankAccountDTOS;
    }

    private static List<BankAccountDTO> getDtoList(List<BankAccountEntity> bankAccountEntityList) {
        List<BankAccountDTO> bankAccountDTOS = new ArrayList<>();
        for (BankAccountEntity bankAccountEntity: bankAccountEntityList) {
            bankAccountDTOS.add(bankAccountEntity.toDTO());
        }
        return bankAccountDTOS;
    }

    public BankAccountEntity toEntity(BankAccountDTO bankAccount) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findById(bankAccount.getId()).orElse(null);
        if(bankAccountEntity == null){
            bankAccountEntity = new BankAccountEntity();
        }
        bankAccountEntity.setId(bankAccount.getId());
        bankAccountEntity.setIban(bankAccount.getIban());
        bankAccountEntity.setActive(bankAccount.isActive());
        bankAccountEntity.setBalance(bankAccount.getBalance());
        bankAccountEntity.setClientByClientId(clientService.toEntidy(bankAccount.getClientByClientId()));
        bankAccountEntity.setBadgeByBadgeId(badgeService.toEntity(bankAccount.getBadgeByBadgeId()));

        return bankAccountEntity;
    }

    public void save(BankAccountDTO bankAccount, CompanyDTO company, RequestDTO request) {
        BankAccountEntity bankAccountEntity = toEntity(bankAccount);

    }
}
