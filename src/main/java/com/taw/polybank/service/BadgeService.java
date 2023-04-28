package com.taw.polybank.service;

import com.taw.polybank.dao.BadgeRepository;
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
    protected BadgeRepository badgeRepository;

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
