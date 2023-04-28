package com.taw.polybank.service;

import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.TransactionRepository;
import com.taw.polybank.dto.BankAccountDTO;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.dto.TransactionDTO;
import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.ClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {
    @Autowired
    private BankAccountRepository bankAccountRepository;

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
        List<BankAccountEntity> bankAccountEntityList = bankAccountRepository.findInactiveAccountsFrom(Timestamp.valueOf(dateTime));
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

    public void blockAccountById(Integer id) {
        Optional<BankAccountEntity> bankAccountEntity = bankAccountRepository.findById(id);
        if (bankAccountEntity.isEmpty())
            return;
        BankAccountEntity bankAccount = bankAccountEntity.get();
        bankAccount.setActive(false);
        bankAccountRepository.save(bankAccount);

    }
}
