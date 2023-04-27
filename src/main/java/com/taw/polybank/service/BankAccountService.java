package com.taw.polybank.service;

import com.taw.polybank.dao.AuthorizedAccountRepository;
import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.AuthorizedAccountEntity;
import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.CompanyEntity;
import com.taw.polybank.entity.RequestEntity;
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
    protected AuthorizedAccountRepository authorizedAccountRepository;

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

    public BankAccountEntity toEntity(BankAccountDTO bankAccount, ClientService clientService, BadgeService badgeService) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findById(bankAccount.getId()).orElse(new BankAccountEntity());
        bankAccountEntity.setId(bankAccount.getId());
        bankAccountEntity.setIban(bankAccount.getIban());
        bankAccountEntity.setActive(bankAccount.isActive());
        bankAccountEntity.setBalance(bankAccount.getBalance());
        bankAccountEntity.setClientByClientId(clientService.toEntidy(bankAccount.getClientByClientId()));
        bankAccountEntity.setBadgeByBadgeId(badgeService.toEntity(bankAccount.getBadgeByBadgeId()));
        return bankAccountEntity;
    }

    public void save(BankAccountDTO bankAccountDTO,
                     CompanyDTO companyDTO, CompanyService companyService,
                     RequestDTO requestDTO, RequestService requestService,
                     ClientService clientService, BadgeService badgeService) {
        BankAccountEntity bankAccount = toEntity(bankAccountDTO, clientService, badgeService);
        CompanyEntity company = companyService.toEntity(companyDTO);
        RequestEntity request = requestService.toEntity(requestDTO);

        if (bankAccount.getCompaniesById() == null) {
            bankAccount.setCompanyById(List.of(company));
        } else {
            bankAccount.getCompaniesById().add(company);
        }

        if (bankAccount.getRequestsById() == null) {
            bankAccount.setRequestsById(List.of(request));
        } else{
            bankAccount.getRequestsById().add(request);
        }

        bankAccountRepository.save(bankAccount);
        bankAccountDTO.setId(bankAccount.getId());
    }

    public int getBankAccountId(BankAccountDTO bankAccount) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findBankAccountEntityByIban(bankAccount.getIban());
        return bankAccountEntity.getId();
    }

    public void addAuthorizedAccount(BankAccountDTO bankAccount, AuthorizedAccountDTO authorizedAccount) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findById(bankAccount.getId()).orElse(null);
        AuthorizedAccountEntity authorizedAccountEntity = authorizedAccountRepository.findById(authorizedAccount.getAuthorizedAccountId()).orElse(null);
        bankAccountEntity.getAuthorizedAccountsById().add(authorizedAccountEntity);
        bankAccountRepository.save(bankAccountEntity);
    }
}
