package com.taw.polybank.service

import com.taw.polybank.dao.CurrencyExchangeRepository
import com.taw.polybank.dto.CurrencyExchangeDTO
import com.taw.polybank.entity.CurrencyExchangeEntity
import org.springframework.stereotype.Service

/**
 * @author Illya Rozumovskyy
 */
@Service
class CurrencyExchangeService(private val currencyExchangeRepository: CurrencyExchangeRepository) {

  fun save(currencyExchangeDTO: CurrencyExchangeDTO, badgeService: BadgeService) {
    val currencyExchange = this.toEntity(currencyExchangeDTO, badgeService)
    currencyExchangeRepository.save<CurrencyExchangeEntity>(currencyExchange)
    currencyExchangeDTO.id = currencyExchange.id
  }

  fun toEntity(
    currencyExchangeDTO: CurrencyExchangeDTO,
    badgeService: BadgeService,
  ): CurrencyExchangeEntity {
    val currencyExchange =
      currencyExchangeRepository
        .findById(currencyExchangeDTO.getId())
        .orElse(CurrencyExchangeEntity())
    currencyExchange.setId(currencyExchangeDTO.getId())
    currencyExchange.setInitialAmount(currencyExchangeDTO.getInitialAmount())
    currencyExchange.setFinalAmount(currencyExchangeDTO.getFinalAmount())
    currencyExchange.setBadgeByInitialBadgeId(
      badgeService.toEntity(currencyExchangeDTO.getBadgeByInitialBadgeId()),
    )
    currencyExchange.setBadgeByFinalBadgeId(
      badgeService.toEntity(currencyExchangeDTO.getBadgeByFinalBadgeId()),
    )
    return currencyExchange
  }
}
