package com.taw.polybank.service;

import com.taw.polybank.dao.AuthorizedAccountRepository;
import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AuthorizedAccountRepository authorizedAccountRepository;

    public List<BankAccountDTO> findByClient(ClientDTO client) {
        ClientEntity clientEntity = clientRepository.findByDNI(client.getDni());
        List<BankAccountEntity> bankAccountEntityList = bankAccountRepository.findByClientByClientId(clientEntity);
        return bankAccountEntityListToDTO(bankAccountEntityList);
    }

    private List<BankAccountDTO> bankAccountEntityListToDTO (List<BankAccountEntity> bankAccountEntityList){
        List<BankAccountDTO> bankAccountDTOList = new ArrayList<>();
        for (BankAccountEntity bankAccountEntity : bankAccountEntityList){
            bankAccountDTOList.add(bankAccountEntity.toDTO());
        }
        return bankAccountDTOList;
    }

    public BankAccountDTO findById(Integer bankAccountId) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findById(bankAccountId).orElse(null);
        return bankAccountEntity == null ? null : bankAccountEntity.toDTO();
    }

    public BankAccountDTO findByIban(String bankAccountIBAN) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findByIban(bankAccountIBAN).orElse(null);
        return bankAccountEntity == null ? null : bankAccountEntity.toDTO();
    }

    public List<BankAccountDTO> findAll() {
        List<BankAccountEntity> bankAccountEntityList = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = getDtoList(bankAccountEntityList);
        return bankAccountDTOS;
    }

    public List<BankAccountDTO> findSuspicious() {
        List<BankAccountEntity> bankAccountEntityList = bankAccountRepository.findSuspiciousTransactionAccount();
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
        for (BankAccountEntity bankAccountEntity : bankAccountEntityList) {
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
        } else {
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
        if (bankAccountEntity.getAuthorizedAccountsById() == null) {
            bankAccountEntity.setAuthorizedAccountsById(List.of(authorizedAccountEntity));
        } else {
            bankAccountEntity.getAuthorizedAccountsById().add(authorizedAccountEntity);
        }
        bankAccountRepository.save(bankAccountEntity);
    }

    public void save(BankAccountDTO bankAccountDTO, ClientService clientService, BadgeService badgeService) {
        BankAccountEntity bankAccount = this.toEntity(bankAccountDTO, clientService, badgeService);
        bankAccountRepository.save(bankAccount);
        bankAccountDTO.setId(bankAccount.getId());
    }

    public BankAccountDTO findBankAccountEntityByIban(String iban) {
        BankAccountEntity bankAccount = bankAccountRepository.findBankAccountEntityByIban(iban);
        if (bankAccount == null) {
            return null;
        } else {
            return bankAccount.toDTO();
        }
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
