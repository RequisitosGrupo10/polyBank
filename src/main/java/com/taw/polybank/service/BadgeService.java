package com.taw.polybank.service;

import com.taw.polybank.dao.BadgeRepository;
import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dto.BadgeDTO;
import com.taw.polybank.dto.BankAccountDTO;
import com.taw.polybank.entity.BadgeEntity;
import com.taw.polybank.entity.BankAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import com.taw.polybank.dto.BadgeDTO;
import com.taw.polybank.dto.CurrencyExchangeDTO;
import com.taw.polybank.entity.BadgeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    public BadgeDTO findByBankAccountsById(BankAccountDTO bankAccount) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findById(bankAccount.getId()).orElse(null);
        if(bankAccountEntity != null){
            BadgeEntity badgeEntity = badgeRepository.findByBankAccountsById(bankAccountEntity);
            return badgeEntity.toDTO();
        }else{
            return null;
        }
    }

    public List<BadgeDTO> findAllBadges() {
        List<BadgeEntity> badgeEntityList = badgeRepository.findAll();
        List<BadgeDTO> badgeDTOList = new ArrayList<>();
        for(BadgeEntity badgeEntity : badgeEntityList){
            badgeDTOList.add(badgeEntity.toDTO());
        }
        return badgeDTOList;
    }

    public BadgeDTO findById(int badgeId) {
        BadgeEntity badgeEntity = badgeRepository.findById(badgeId).orElse(null);
        return badgeEntity == null ? null : badgeEntity.toDTO();
    }

    public List<BadgeDTO> findAll() {
        return badgeRepository.findAll().stream().map(badge -> badge.toDTO()).collect(Collectors.toList());
    }

    public BadgeEntity toEntity(BadgeDTO badge) {
        BadgeEntity badgeEntity = badgeRepository.findById(badge.getId()).orElse(null);
        return badgeEntity;
    }

    public BadgeDTO findById(Integer badgeId) {
        return badgeRepository.findById(badgeId).orElse(null).toDTO();
    }

    public void addAndSave(BadgeDTO badgeDTO, CurrencyExchangeDTO currencyExchange) {
        // TODO add currency exchange to badge
    }

    public BadgeDTO getRandomBadge() {
        List<BadgeEntity> allBadges = badgeRepository.findAll();
        return allBadges.get(new java.util.Random().nextInt(allBadges.size())).toDTO();
    }

    public BadgeDTO findBadgeEntityByName(String badge) {
        return badgeRepository.findBadgeEntityByName(badge).toDTO();
    }
}
