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

    public List<AuthorizedAccountDTO> findAuthorizedAccountEntitiesOfGivenBankAccount(int bankAccountId){
        return authorizedAccountRepository.findAuthorizedAccountEntitiesOfGivenBankAccount(bankAccountId)
                .stream()
                .map(authAcc -> authAcc.toDto())
                .collect(Collectors.toList());
    }
}
