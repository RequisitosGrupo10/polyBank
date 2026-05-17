package com.taw.polybank.service

import com.taw.polybank.dao.BadgeRepository
import com.taw.polybank.dao.BankAccountRepository
import com.taw.polybank.dto.BadgeDTO
import com.taw.polybank.dto.BankAccountDTO
import com.taw.polybank.dto.CurrencyExchangeDTO
import com.taw.polybank.entity.BadgeEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

/**
 * @author Illya Rozumovskyy 67%
 * @author Lucía Gutiérrez Molina 33%
 */
@Service
class BadgeService(
  private val bankAccountRepository: BankAccountRepository,
  private val badgeRepository: BadgeRepository,
) {
  fun findByBankAccountsById(bankAccount: BankAccountDTO): BadgeDTO {
    val bankAccountEntity =
      bankAccountRepository.findById(bankAccount.id).orElse(null)
    if (bankAccountEntity != null) {
      val badgeEntity = badgeRepository.findByBankAccountsById(bankAccountEntity)
      return badgeEntity.toDTO()
    } else {
      return BadgeDTO()
    }
  }

  fun findAllBadges(): MutableList<BadgeDTO?> {
    val badgeEntityList = badgeRepository.findAll()
    val badgeDTOList: MutableList<BadgeDTO?> = ArrayList<BadgeDTO?>()
    for (badgeEntity in badgeEntityList) {
      badgeDTOList.add(badgeEntity.toDTO())
    }
    return badgeDTOList
  }

  fun findById(badgeId: Int): BadgeDTO = badgeRepository.findById(badgeId).orElse(BadgeEntity()).toDTO()

  fun findAll(): MutableList<BadgeDTO?> = badgeRepository.findAll().stream()
    .map { badge: BadgeEntity -> badge.toDTO() }
    .collect(Collectors.toList())

  fun toEntity(badge: BadgeDTO): BadgeEntity? {
    val badgeEntity = badgeRepository.findById(badge.getId()).orElse(null)
    return badgeEntity
  }

  fun addAndSave(badgeDTO: BadgeDTO?, currencyExchange: CurrencyExchangeDTO?) {
    // TODO add currency exchange to badge
  }

  val randomBadge: BadgeDTO?
    get() {
      val allBadges = badgeRepository.findAll()
      return allBadges.get(Random().nextInt(allBadges.size)).toDTO()
    }

  fun findBadgeEntityByName(badge: String?): BadgeDTO? = badgeRepository.findBadgeEntityByName(badge).toDTO()
}
