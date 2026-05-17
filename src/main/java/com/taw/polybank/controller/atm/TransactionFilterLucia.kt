package com.taw.polybank.controller.atm

import java.sql.Date

/**
 * @author Lucía Gutiérrez Molina
 */
class TransactionFilterLucia(
  @JvmField var timestampBegin: Date?,
  @JvmField var timestampEnd: Date?,
  @JvmField var transactionOwner: String?,
  @JvmField var beneficiaryIban: String?,
  private var amountString: String?,
) {
  var amount: Double?
    private set

  init {
    this.amount = (if (amountString == null || amountString == "") "0" else amountString)!!.toDouble()
  }

  fun setAmountString(amount: String?) {
    if (amount == null || amount == "") {
      this.amountString = "0.0"
      this.amount = 0.0
    } else {
      this.amountString = amount
      this.amount = amount.toDouble()
    }
  }
}
