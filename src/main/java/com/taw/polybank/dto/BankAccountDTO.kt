package com.taw.polybank.dto

import com.taw.polybank.entity.BankAccountEntity

/**
 * @author José Manuel Sánchez Rico
 */
data class BankAccountDTO(
  var id: Int = 0,
  var iban: String = "",
  var isActive: Boolean = false,
  var balance: Double = 0.0,
  var clientByClientId: ClientDTO? = null,
  var badgeByBadgeId: BadgeDTO? = null,
) {
  constructor(account: BankAccountEntity) : this() {
    this.id = account.id
    this.iban = account.iban
    this.isActive = account.isActive
    this.balance = account.balance
    this.clientByClientId = ClientDTO(account.clientByClientId)
    this.badgeByBadgeId = BadgeDTO(account.badgeByBadgeId)
  }
}
