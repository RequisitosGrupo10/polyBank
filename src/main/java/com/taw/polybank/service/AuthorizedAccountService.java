package com.taw.polybank.service;

import com.taw.polybank.dao.AuthorizedAccountRepository;
import com.taw.polybank.dto.AuthorizedAccountDTO;
import com.taw.polybank.entity.AuthorizedAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorizedAccountService {

    @Autowired
    protected AuthorizedAccountRepository authorizedAccountRepository;

    public List<AuthorizedAccountDTO> findAuthorizedAccountEntitiesOfGivenBankAccount(int bankAccountId) {
        return authorizedAccountRepository.findAuthorizedAccountEntitiesOfGivenBankAccount(bankAccountId)
                .stream()
                .map(authAcc -> authAcc.toDto())
                .collect(Collectors.toList());
    }

    public AuthorizedAccountEntity toEntity(AuthorizedAccountDTO authorizedAccount) {
        AuthorizedAccountEntity accountEntity = authorizedAccountRepository.findById(authorizedAccount.getAuthorizedAccountId()).orElse(new AuthorizedAccountEntity());
        accountEntity.setAuthorizedAccountId(authorizedAccount.getAuthorizedAccountId());
        accountEntity.setBlocked(authorizedAccount.isBlocked());
        return accountEntity;
    }

    public void save(AuthorizedAccountDTO authorizedAccount, ClientService clientService, BankAccountService bankAccountService, BadgeService badgeService) {
        AuthorizedAccountEntity accountEntity = this.toEntity(authorizedAccount);
        accountEntity.setClientByClientId(clientService.toEntidy(authorizedAccount.getClientByClientId()));
        accountEntity.setBankAccountByBankAccountId(bankAccountService.toEntity(authorizedAccount.getBankAccountByBankAccountId(), clientService, badgeService));

        authorizedAccountRepository.save(accountEntity);
        authorizedAccount.setAuthorizedAccountId(accountEntity.getAuthorizedAccountId());
    }

    public void findAndBlockAuthAccOfGivenClientAndCompany(Integer clientId, Integer companyId) {
        AuthorizedAccountEntity authorizedAccount = authorizedAccountRepository.findAuthAccOfGivenClientAndCompany(clientId, companyId);
        authorizedAccount.setBlocked(true);
        authorizedAccountRepository.save(authorizedAccount);
    }
}
