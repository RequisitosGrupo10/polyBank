package com.taw.polybank.service

import com.taw.polybank.dao.AuthorizedAccountRepository
import com.taw.polybank.dto.AuthorizedAccountDTO
import com.taw.polybank.entity.AuthorizedAccountEntity
import org.springframework.stereotype.Service
import java.util.stream.Collectors

/**
 * @author Illya Rozumovskyy
 */
@Service
class AuthorizedAccountService(private val authorizedAccountRepository: AuthorizedAccountRepository) {

  fun findAuthorizedAccountEntitiesOfGivenBankAccount(
    bankAccountId: Int,
  ): MutableList<AuthorizedAccountDTO?> = authorizedAccountRepository
    .findAuthorizedAccountEntitiesOfGivenBankAccount(bankAccountId)
    .stream()
    .map { authAcc: AuthorizedAccountEntity? -> authAcc!!.toDto() }
    .collect(Collectors.toList())

  fun toEntity(authorizedAccount: AuthorizedAccountDTO): AuthorizedAccountEntity {
    val accountEntity =
      authorizedAccountRepository
        .findById(authorizedAccount.getAuthorizedAccountId())
        .orElse(AuthorizedAccountEntity())
    accountEntity.setAuthorizedAccountId(authorizedAccount.getAuthorizedAccountId())
    accountEntity.setBlocked(authorizedAccount.isBlocked())
    return accountEntity
  }

  fun save(
    authorizedAccount: AuthorizedAccountDTO,
    clientService: ClientService,
    bankAccountService: BankAccountService,
    badgeService: BadgeService,
  ) {
    val accountEntity = this.toEntity(authorizedAccount)
    accountEntity.setClientByClientId(
      clientService.toEntidy(authorizedAccount.getClientByClientId()),
    )
    accountEntity.setBankAccountByBankAccountId(
      bankAccountService.toEntity(
        authorizedAccount.getBankAccountByBankAccountId(),
        clientService,
        badgeService,
      ),
    )

    authorizedAccountRepository.save<AuthorizedAccountEntity>(accountEntity)
    authorizedAccount.setAuthorizedAccountId(accountEntity.getAuthorizedAccountId())
  }

  fun findAndBlockAuthAccOfGivenClientAndCompany(clientId: Int?, companyId: Int?) {
    val authorizedAccount =
      authorizedAccountRepository.findAuthAccOfGivenClientAndCompany(clientId, companyId)
    authorizedAccount.setBlocked(true)
    authorizedAccountRepository.save(authorizedAccount)
  }
}
