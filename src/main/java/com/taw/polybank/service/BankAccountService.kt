package com.taw.polybank.service

import com.taw.polybank.dao.AuthorizedAccountRepository
import com.taw.polybank.dao.BankAccountRepository
import com.taw.polybank.dao.ClientRepository
import com.taw.polybank.dto.AuthorizedAccountDTO
import com.taw.polybank.dto.BankAccountDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.dto.CompanyDTO
import com.taw.polybank.dto.RequestDTO
import com.taw.polybank.entity.AuthorizedAccountEntity
import com.taw.polybank.entity.BankAccountEntity
import com.taw.polybank.entity.CompanyEntity
import com.taw.polybank.entity.RequestEntity
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.List

/**
 * @author Illya Rozumovskyy 40%
 * @author Lucía Gutiérrez Molina 27%
 * @author José Manuel Sánchez Rico 33%
 */
@Service
class BankAccountService(
  private val bankAccountRepository: BankAccountRepository,
  private val clientRepository: ClientRepository,
  private val authorizedAccountRepository: AuthorizedAccountRepository,
) {
  fun findByClient(client: ClientDTO): MutableList<BankAccountDTO?> {
    val clientEntity = clientRepository.findByDNI(client.getDni())
    val bankAccountEntityList =
      bankAccountRepository.findByClientByClientId(clientEntity)
    return bankAccountEntityListToDTO(bankAccountEntityList)
  }

  private fun bankAccountEntityListToDTO(
    bankAccountEntityList: MutableList<BankAccountEntity>,
  ): MutableList<BankAccountDTO?> {
    val bankAccountDTOList: MutableList<BankAccountDTO?> = ArrayList()
    for (bankAccountEntity in bankAccountEntityList) {
      bankAccountDTOList.add(bankAccountEntity.toDTO())
    }
    return bankAccountDTOList
  }

  fun findById(bankAccountId: Int): BankAccountDTO = bankAccountRepository.findById(bankAccountId).orElse(BankAccountEntity()).toDTO()

  fun findByIban(bankAccountIBAN: String?): BankAccountDTO? {
    val bankAccountEntity =
      bankAccountRepository.findByIban(bankAccountIBAN).orElse(null)
    return if (bankAccountEntity == null) null else bankAccountEntity.toDTO()
  }

  fun findAll(): MutableList<BankAccountDTO?> {
    val bankAccountEntityList = bankAccountRepository.findAll()
    val bankAccountDTOS: MutableList<BankAccountDTO?> = getDtoList(bankAccountEntityList)
    return bankAccountDTOS
  }

  fun findSuspicious(): MutableList<BankAccountDTO?> {
    val bankAccountEntityList =
      bankAccountRepository.findSuspiciousTransactionAccount()
    val bankAccountDTOS: MutableList<BankAccountDTO?> = getDtoList(bankAccountEntityList)
    return bankAccountDTOS
  }

  fun findInactive(): MutableList<BankAccountDTO?> {
    val timestamp = Timestamp.from(Instant.now())
    val dateTime = LocalDateTime.now().minusMonths(1)
    val bankAccountEntityList =
      bankAccountRepository.findInactiveAccountsFrom(Timestamp.valueOf(dateTime))
    val bankAccountDTOS: MutableList<BankAccountDTO?> = getDtoList(bankAccountEntityList)
    return bankAccountDTOS
  }

  fun toEntity(
    bankAccount: BankAccountDTO,
    clientService: ClientService,
    badgeService: BadgeService,
  ): BankAccountEntity {
    val bankAccountEntity =
      bankAccountRepository.findById(bankAccount.id).orElse(BankAccountEntity())
    bankAccountEntity.setId(bankAccount.id)
    bankAccountEntity.setIban(bankAccount.iban)
    bankAccountEntity.setActive(bankAccount.isActive)
    bankAccountEntity.setBalance(bankAccount.balance)
    bankAccountEntity.setClientByClientId(
      clientService.toEntidy(bankAccount.clientByClientId!!),
    )
    bankAccountEntity.setBadgeByBadgeId(badgeService.toEntity(bankAccount.badgeByBadgeId))
    return bankAccountEntity
  }

  fun save(
    bankAccountDTO: BankAccountDTO,
    companyDTO: CompanyDTO,
    companyService: CompanyService,
    requestDTO: RequestDTO,
    requestService: RequestService,
    clientService: ClientService,
    badgeService: BadgeService,
  ) {
    val bankAccount = toEntity(bankAccountDTO, clientService, badgeService)
    val company = companyService.toEntity(companyDTO)
    val request = requestService.toEntity(requestDTO)

    if (bankAccount.getCompaniesById() == null) {
      bankAccount.setCompanyById(List.of<CompanyEntity?>(company))
    } else {
      bankAccount.getCompaniesById().add(company)
    }

    if (bankAccount.getRequestsById() == null) {
      bankAccount.setRequestsById(List.of<RequestEntity?>(request))
    } else {
      bankAccount.getRequestsById().add(request)
    }

    bankAccountRepository.save<BankAccountEntity>(bankAccount)
    bankAccountDTO.id = bankAccount.getId()
  }

  fun getBankAccountId(bankAccount: BankAccountDTO): Int {
    val bankAccountEntity =
      bankAccountRepository.findBankAccountEntityByIban(bankAccount.iban)
    return bankAccountEntity.getId()
  }

  fun addAuthorizedAccount(
    bankAccount: BankAccountDTO,
    authorizedAccount: AuthorizedAccountDTO,
  ) {
    val bankAccountEntity =
      bankAccountRepository.findById(bankAccount.id).orElse(null)
    val authorizedAccountEntity =
      authorizedAccountRepository
        .findById(authorizedAccount.getAuthorizedAccountId())
        .orElse(null)
    if (bankAccountEntity.getAuthorizedAccountsById() == null) {
      bankAccountEntity.setAuthorizedAccountsById(List.of<AuthorizedAccountEntity?>(authorizedAccountEntity))
    } else {
      bankAccountEntity.getAuthorizedAccountsById().add(authorizedAccountEntity)
    }
    bankAccountRepository.save(bankAccountEntity)
  }

  fun save(
    bankAccountDTO: BankAccountDTO,
    clientService: ClientService,
    badgeService: BadgeService,
  ) {
    val bankAccount = this.toEntity(bankAccountDTO, clientService, badgeService)
    bankAccountRepository.save<BankAccountEntity>(bankAccount)
    bankAccountDTO.id = bankAccount.getId()
  }

  fun findBankAccountEntityByIban(iban: String?): BankAccountDTO? {
    val bankAccount = bankAccountRepository.findBankAccountEntityByIban(iban)
    if (bankAccount == null) {
      return null
    } else {
      return bankAccount.toDTO()
    }
  }

  fun blockAccountById(id: Int) {
    val bankAccountEntity = bankAccountRepository.findById(id)
    if (bankAccountEntity.isEmpty()) return
    val bankAccount = bankAccountEntity.get()
    bankAccount.setActive(false)
    bankAccountRepository.save<BankAccountEntity>(bankAccount)
  }

  companion object {
    private fun getDtoList(bankAccountEntityList: MutableList<BankAccountEntity>): MutableList<BankAccountDTO?> {
      val bankAccountDTOS: MutableList<BankAccountDTO?> = ArrayList()
      for (bankAccountEntity in bankAccountEntityList) {
        bankAccountDTOS.add(bankAccountEntity.toDTO())
      }
      return bankAccountDTOS
    }
  }
}
