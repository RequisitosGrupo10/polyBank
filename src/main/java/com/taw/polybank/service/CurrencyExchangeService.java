package com.taw.polybank.service;

import com.taw.polybank.dao.CurrencyExchangeRepository;
import com.taw.polybank.dto.CurrencyExchangeDTO;
import com.taw.polybank.entity.CurrencyExchangeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyExchangeService {

    @Autowired
    protected CurrencyExchangeRepository currencyExchangeRepository;

    public void save(CurrencyExchangeDTO currencyExchangeDTO, BadgeService badgeService) {
        CurrencyExchangeEntity currencyExchange = this.toEntity(currencyExchangeDTO, badgeService);
        currencyExchangeRepository.save(currencyExchange);
        currencyExchangeDTO.setId(currencyExchange.getId());
    }

    public CurrencyExchangeEntity toEntity(CurrencyExchangeDTO currencyExchangeDTO, BadgeService badgeService) {
        CurrencyExchangeEntity currencyExchange = currencyExchangeRepository.findById(currencyExchangeDTO.getId()).orElse(new CurrencyExchangeEntity());
        currencyExchange.setId(currencyExchangeDTO.getId());
        currencyExchange.setInitialAmount(currencyExchangeDTO.getInitialAmount());
        currencyExchange.setFinalAmount(currencyExchangeDTO.getFinalAmount());
        currencyExchange.setBadgeByInitialBadgeId(badgeService.toEntity(currencyExchangeDTO.getBadgeByInitialBadgeId()));
        currencyExchange.setBadgeByFinalBadgeId(badgeService.toEntity(currencyExchangeDTO.getBadgeByFinalBadgeId()));
        return currencyExchange;
    }
}
