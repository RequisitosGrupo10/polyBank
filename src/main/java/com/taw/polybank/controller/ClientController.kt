package com.taw.polybank.controller

import com.taw.polybank.dao.ClientRepository
import com.taw.polybank.dto.BadgeDTO
import com.taw.polybank.dto.BankAccountDTO
import com.taw.polybank.dto.BenficiaryDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.dto.CurrencyExchangeDTO
import com.taw.polybank.dto.PaymentDTO
import com.taw.polybank.dto.TransactionDTO
import com.taw.polybank.entity.RequestEntity
import com.taw.polybank.service.BadgeService
import com.taw.polybank.service.BankAccountService
import com.taw.polybank.service.BeneficiaryService
import com.taw.polybank.service.ClientService
import com.taw.polybank.service.CompanyService
import com.taw.polybank.service.CurrencyExchangeService
import com.taw.polybank.service.PaymentService
import com.taw.polybank.service.RequestService
import com.taw.polybank.service.TransactionService
import com.taw.polybank.ui.companyFilters.TransactionFilterIllya
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.sql.Timestamp
import java.time.Instant
import java.util.Random

/**
 * @author Pablo Ruiz-Cruces
 */
@Controller
@RequestMapping("/client")
class ClientController(
  private val clientRepository: ClientRepository,
  private val bankAccountService: BankAccountService,
  private val clientService: ClientService,
  private val companyService: CompanyService,
  private val badgeService: BadgeService,
  private val currencyExchangeService: CurrencyExchangeService,
  private val transactionService: TransactionService,
  private val beneficiaryService: BeneficiaryService,
  private val paymentService: PaymentService,
  private val requestService: RequestService,
) {
  @GetMapping("/view")
  fun viewClient(model: Model, session: HttpSession): String {
    val clientDTO = session.getAttribute("client") as ClientDTO?
    if (clientDTO == null) return "redirect:/"
    val accounts = bankAccountService.findByClient(clientDTO)
    model.addAttribute("client", clientDTO)
    model.addAttribute("accounts", accounts)
    return "client/viewData"
  }

  @GetMapping("/edit")
  fun editClient(model: Model, session: HttpSession): String {
    val clientDTO = session.getAttribute("client") as ClientDTO?
    if (clientDTO == null) return "redirect:/"
    model.addAttribute("client", clientDTO)
    return "client/editData"
  }

  @PostMapping("/edit")
  fun saveClient(@ModelAttribute("client") client: ClientDTO, session: HttpSession): String {
    this.clientService.save(client)
    session.setAttribute("client", client)
    return "redirect:/client/view"
  }

  @GetMapping("/register")
  fun registerClient(model: Model): String {
    val client = ClientDTO()
    model.addAttribute("client", client)
    return "client/register"
  }

  @PostMapping("/register")
  fun addClient(
    @ModelAttribute("client") client: ClientDTO,
    @RequestParam("password") password: String?,
  ): String {
    val account = BankAccountDTO()
    defineBankAccount(account)
    client.setCreationDate(Timestamp.from(Instant.now()))
    val passwordManager = PasswordManager(clientService)
    val saltAndPass = passwordManager.savePassword(client, password)
    account.clientByClientId = client
    clientService.save(client, saltAndPass)
    bankAccountService.save(account, clientService, badgeService)
    return "redirect:/"
  }

  @GetMapping("/account")
  fun viewBankAccount(
    @RequestParam("id") accountID: Int,
    model: Model?,
    session: HttpSession,
  ): String {
    val clientDTO = session.getAttribute("client") as ClientDTO?
    if (clientDTO == null) return "redirect:/"
    val account = bankAccountService.findById(accountID)

    if (account.clientByClientId == clientDTO) {
      session.setAttribute("account", account)
      return "client/bankAccount/viewData"
    } else {
      println("ERROR: No accesible.")
      return "redirect:/client/view"
    }
  }

  @GetMapping("/account/transaction")
  fun transferMoneyOnBankAccount(model: Model?, session: HttpSession?): String = "client/bankAccount/makeTransfer"

  @PostMapping("/account/processTransfer")
  fun processTransfer(
    @RequestParam("beneficiary") beneficiaryName: String?,
    @RequestParam("iban") iban: String?,
    @RequestParam("amount") amount: Double,
    session: HttpSession,
    model: Model,
  ): String {
    if (amount <= 0) {
      // fail message negative or zero amount
      model.addAttribute(
        "message",
        "Money transfer was denied, invalid amount (amount = " + amount + ")",
      )
      return "redirect:/account"
    }

    val clientDTO = session.getAttribute("client") as ClientDTO?
    if (clientDTO == null) return "redirect:/"
    val account = session.getAttribute("account") as BankAccountDTO?
    if (account == null || !account.isActive) return "redirect:/"
    if (account.balance < amount) {
      // fail message not enough money
      model.addAttribute(
        "message",
        "Money transfer was unsuccessful, not enough money in your bank account",
      )
      return "redirect:/client/account?id=" + account.id
    }

    val originBadge = account.badgeByBadgeId
    var recipientBadge: BadgeDTO = BadgeDTO()
    val transaction = defineTransaction(clientDTO, account)
    var beneficiary =
      beneficiaryService.findBenficiaryByNameAndIban(beneficiaryName, iban)
    val payment = definePayment(amount, beneficiary)
    val recipientBankAccount = bankAccountService.findBankAccountEntityByIban(iban)

    if (recipientBankAccount != null) { // Internal bank money transfer
      val companyRecipient = companyService.findCompanyByName(beneficiaryName)
      if (companyRecipient
        == null
      ) { // Private Client is going to receive money, Authorized person can not figure
        // as beneficiary only proper owner of the account.
        val clientRecipient = recipientBankAccount.clientByClientId
        if (clientRecipient?.getName() != beneficiaryName) {
          // fail message name is not matching
          model.addAttribute(
            "message",
            "Money transfer was unsuccessful, recipient name is not correct",
          )
          return "redirect:/client/account?id=" + account.id
        }
        // name matching proceed to transfer.
      } // Company is going to receive money

      recipientBadge = recipientBankAccount.badgeByBadgeId

      if (beneficiary == null) {
        beneficiary = defineBeneficiary(beneficiaryName, iban, recipientBadge)
      }

      payment.setBenficiaryByBenficiaryId(beneficiary)

      if (originBadge.getId() != recipientBadge.getId()) { // Do we need currency exchange?
        val currencyExchange =
          defineCurrencyExchange(originBadge, recipientBadge, amount, transaction, payment)
        recipientBankAccount.balance = recipientBankAccount.balance + currencyExchange.getFinalAmount()
      } else {
        recipientBankAccount.balance = recipientBankAccount.balance + amount
      }
      transaction.setPaymentByPaymentId(payment)
      bankAccountService.save(recipientBankAccount, clientService, badgeService)
    } else { // External bank money transfer
      if (beneficiary == null) {
        recipientBadge =
          badgeService
            .randomBadge // Assign random badge due to it unknown, and we can simulate
        // this way international transactions
        beneficiary = defineBeneficiary(beneficiaryName, iban, recipientBadge)
      } else {
        recipientBadge = badgeService.findBadgeEntityByName(beneficiary.getBadge())
      }
      payment.setBenficiaryByBenficiaryId(beneficiary)

      if (originBadge.getId() != recipientBadge.getId()) { // Do we need currency exchange?
        val currencyExchange =
          defineCurrencyExchange(originBadge, recipientBadge, amount, transaction, payment)
      }
      transaction.setPaymentByPaymentId(payment)
    }
    beneficiaryService.save(beneficiary)
    paymentService.save(payment, beneficiaryService, currencyExchangeService, badgeService)
    transactionService.save(
      transaction,
      clientService,
      bankAccountService,
      currencyExchangeService,
      paymentService,
      badgeService,
      beneficiaryService,
    )

    account.balance = account.balance - amount
    bankAccountService.save(account, clientService, badgeService)

    // success message
    model.addAttribute(
      "message",
      (
        amount
          .toString() + " " +
          account.badgeByBadgeId.getName() +
          " was successfully transferred to " +
          beneficiaryName
        ),
    )
    return "redirect:/client/account?id=" + account.id
  }

  @GetMapping("/account/moneyExchange")
  fun moneyExchangeOnBankAccount(model: Model, session: HttpSession): String {
    val badgeList = badgeService.findAll()
    val account = session.getAttribute("account") as BankAccountDTO?
    if (account == null || !account.isActive) return "redirect:/"
    model.addAttribute("badgeList", badgeList)
    model.addAttribute("account", account)
    return "client/bankAccount/moneyExchange"
  }

  @PostMapping("/account/makeExchange")
  fun makeExchange(
    @RequestParam("badge") badgeID: Int,
    session: HttpSession,
    model: Model?,
  ): String {
    val clientDTO = session.getAttribute("client") as ClientDTO?
    val account = session.getAttribute("account") as BankAccountDTO?
    if (clientDTO == null || account == null) return "redirect:/"
    val currentBadge = account.badgeByBadgeId
    val targetBadge = badgeService.findById(badgeID)

    val transaction = defineTransaction(clientDTO, account)
    var beneficiary =
      beneficiaryService.findBenficiaryByNameAndIban(clientDTO.getName(), account.iban)

    if (beneficiary == null) beneficiary = defineBeneficiary(clientDTO.getName(), account.iban, targetBadge)
    val payment = definePayment(account.balance, beneficiary)

    payment.setBenficiaryByBenficiaryId(beneficiary)

    if (currentBadge.getId() != targetBadge.getId()) {
      val currencyExchange =
        defineCurrencyExchange(
          currentBadge,
          targetBadge,
          account.balance,
          transaction,
          payment,
        )
      transaction.setPaymentByPaymentId(payment)
      account.balance = currencyExchange.getFinalAmount()
      account.badgeByBadgeId = targetBadge
      beneficiary.setBadge(targetBadge.getName())
      beneficiary.setSwift("XXX" + targetBadge.getName() + "BNK")
      beneficiaryService.save(beneficiary)
      paymentService.save(payment, beneficiaryService, currencyExchangeService, badgeService)
      transactionService.save(
        transaction,
        clientService,
        bankAccountService,
        currencyExchangeService,
        paymentService,
        badgeService,
        beneficiaryService,
      )
      bankAccountService.save(account, clientService, badgeService)
    } else {
      println("ERROR: Can't change currency to the same badge.")
    }

    return "redirect:/client/account?id=" + account.id
  }

  @GetMapping("/account/operationHistory")
  fun operationHistory(session: HttpSession, model: Model): String = operationHistoryFilters(null, session, model)

  @PostMapping("/account/operationHistory")
  fun operationHistory(
    @ModelAttribute("transactionFilter") transactionFilter: TransactionFilterIllya?,
    session: HttpSession,
    model: Model,
  ): String = operationHistoryFilters(transactionFilter, session, model)

  private fun operationHistoryFilters(
    transactionFilter: TransactionFilterIllya?,
    session: HttpSession,
    model: Model,
  ): String {
    var transactionFilter = transactionFilter
    val transactionList: MutableList<TransactionDTO?>?
    val account = session.getAttribute("account") as BankAccountDTO?

    if (account == null || !account.isActive) return "redirect:/"

    if (transactionFilter == null) {
      transactionList =
        transactionService.findTransactionsByBankAccountByBankAccountIdId(account.id)
      transactionFilter = TransactionFilterIllya()
    } else {
      val dateAfter = Timestamp(transactionFilter.getTransactionAfter().getTime())
      val dateBefore = Timestamp(transactionFilter.getTransactionBefore().getTime())
      if (transactionFilter.getSenderId().isBlank() &&
        transactionFilter.getRecipientName().isBlank()
      ) {
        transactionList =
          transactionService.findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
            account.id,
            dateAfter,
            dateBefore,
            transactionFilter.getMinAmount(),
            transactionFilter.getMaxAmount(),
          )
      } else if (!transactionFilter.getSenderId().isBlank() &&
        transactionFilter.getRecipientName().isBlank()
      ) {
        transactionList =
          transactionService
            .findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
              account.id,
              dateAfter,
              dateBefore,
              transactionFilter.getMinAmount(),
              transactionFilter.getMaxAmount(),
              transactionFilter.getSenderId(),
            )
      } else if (transactionFilter.getSenderId().isBlank() &&
        !transactionFilter.getRecipientName().isBlank()
      ) {
        transactionList =
          transactionService
            .findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
              account.id,
              dateAfter,
              dateBefore,
              transactionFilter.getMinAmount(),
              transactionFilter.getMaxAmount(),
              transactionFilter.getRecipientName(),
            )
      } else {
        transactionList =
          transactionService
            .findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
              account.id,
              dateAfter,
              dateBefore,
              transactionFilter.getMinAmount(),
              transactionFilter.getMaxAmount(),
              transactionFilter.getSenderId(),
              transactionFilter.getRecipientName(),
            )
      }
    }
    model.addAttribute("transactionFilter", transactionFilter)
    model.addAttribute("transactionList", transactionList)
    return "client/bankAccount/operationHistory"
  }

  @GetMapping("/account/request")
  fun doRequestUnban(session: HttpSession, model: Model): String {
    val client = session.getAttribute("client") as ClientDTO?
    val account = session.getAttribute("account") as BankAccountDTO?
    if (client == null || account == null) return "redirect:/"

    val requestsNotSolved =
      requestService.findByBankAccountByBankAccountIdAndAndSolved(account, false)
    model.addAttribute("hasRequest", requestsNotSolved.size)

    return "client/bankAccount/requestActivation"
  }

  @GetMapping("/account/makeRequest")
  fun doMakeUnbanPetition(
    @RequestParam("description") description: String?,
    session: HttpSession,
  ): String {
    val client = session.getAttribute("client") as ClientDTO?
    val account = session.getAttribute("account") as BankAccountDTO?
    if (client == null || account == null) return "redirect:/"
    val requestsNotSolved =
      requestService.findByBankAccountByBankAccountIdAndAndSolved(account, false)
    if (requestsNotSolved.size <= 0) {
      requestService.createNewRequest(
        client,
        account,
        RequestEntity.RequestType.ACTIVATION,
        description,
      )
    }
    return "redirect:/client/account?id=" + account.id
  }

  @GetMapping("/logout")
  fun logout(model: Model?, session: HttpSession): String {
    session.invalidate()
    return "redirect:/"
  }

  @PostMapping("/login")
  fun postLogin(
    @RequestParam("dni") dni: String?,
    @RequestParam("password") password: String?,
    session: HttpSession,
  ): String {
    val client = clientRepository.findByDNI(dni)
    if (client != null) {
      // BCrypt.checkpw(password + client.getSalt(), client.getPassword());
      val clientDTO = ClientDTO(client)
      session.setAttribute("client", clientDTO)
      return "redirect:/client/view"
    }
    return ("redirect:/login")
  }

  private fun defineBankAccount(bankAccount: BankAccountDTO) {
    bankAccount.isActive = false
    bankAccount.badgeByBadgeId = badgeService.findBadgeEntityByName("USD")
    val random = Random()
    val iban = StringBuilder()
    iban.append("ES44 5268 3000 ")
    for (i in 0..11) {
      iban.append(random.nextInt(10))
    }
    bankAccount.balance = 0.0
    bankAccount.iban = iban.toString()
  }

  private fun definePayment(amount: Double, beneficiary: BenficiaryDTO?): PaymentDTO {
    val payment = PaymentDTO()
    payment.setAmount(amount)
    payment.setBenficiaryByBenficiaryId(beneficiary)
    return payment
  }

  private fun defineTransaction(client: ClientDTO?, bankAccount: BankAccountDTO?): TransactionDTO {
    val transaction = TransactionDTO()
    transaction.setTimestamp(Timestamp.from(Instant.now()))
    transaction.setClientByClientId(client)
    transaction.setBankAccountByBankAccountId(bankAccount)
    return transaction
  }

  private fun defineCurrencyExchange(
    originBadge: BadgeDTO,
    recipientBadge: BadgeDTO,
    amount: Double,
    transaction: TransactionDTO,
    payment: PaymentDTO,
  ): CurrencyExchangeDTO {
    val currencyExchange = CurrencyExchangeDTO()
    currencyExchange.setBadgeByInitialBadgeId(originBadge)
    currencyExchange.setBadgeByFinalBadgeId(recipientBadge)
    currencyExchange.setInitialAmount(amount)
    val amountAfterExchange = (recipientBadge.getValue() / originBadge.getValue()) * amount
    currencyExchange.setFinalAmount(amountAfterExchange)
    payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchange)
    transaction.setCurrencyExchangeByCurrencyExchangeId(currencyExchange)
    currencyExchangeService.save(currencyExchange, badgeService)
    return currencyExchange
  }

  private fun defineBeneficiary(
    beneficiaryName: String?,
    iban: String?,
    recipientBadge: BadgeDTO,
  ): BenficiaryDTO {
    val beneficiary = BenficiaryDTO()
    beneficiary.setIban(iban)
    beneficiary.setName(beneficiaryName)
    beneficiary.setBadge(recipientBadge.getName())
    beneficiary.setSwift("XXX" + recipientBadge.getName() + "BNK")
    return beneficiary
  }
}
