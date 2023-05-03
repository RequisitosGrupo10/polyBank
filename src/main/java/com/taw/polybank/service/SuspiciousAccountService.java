package com.taw.polybank.service;

import com.taw.polybank.dao.SuspiciousAccountRepository;
import com.taw.polybank.dto.BankAccountDTO;
import com.taw.polybank.entity.SuspiciousAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuspiciousAccountService {

    @Autowired
    private SuspiciousAccountRepository suspiciousAccountRepository;

    public boolean isSuspicious(String bankAccountReceiverIBAN) {
        SuspiciousAccountEntity suspiciousAccountEntity = suspiciousAccountRepository.findByIban(bankAccountReceiverIBAN).orElse(null);
        return suspiciousAccountEntity != null;
    }
}
