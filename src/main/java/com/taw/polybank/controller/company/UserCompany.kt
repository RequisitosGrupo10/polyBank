package com.taw.polybank.controller.company

import com.taw.polybank.controller.PasswordManager
import com.taw.polybank.dto.AuthorizedAccountDTO
import com.taw.polybank.dto.BadgeDTO
import com.taw.polybank.dto.BankAccountDTO
import com.taw.polybank.dto.BenficiaryDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.dto.CompanyDTO
import com.taw.polybank.dto.CurrencyExchangeDTO
import com.taw.polybank.dto.PaymentDTO
import com.taw.polybank.dto.RequestDTO
import com.taw.polybank.dto.TransactionDTO
import com.taw.polybank.entity.RequestEntity
import com.taw.polybank.service.AuthorizedAccountService
import com.taw.polybank.service.BadgeService
import com.taw.polybank.service.BankAccountService
import com.taw.polybank.service.BeneficiaryService
import com.taw.polybank.service.ClientService
import com.taw.polybank.service.CompanyService
import com.taw.polybank.service.CurrencyExchangeService
import com.taw.polybank.service.EmployeeService
import com.taw.polybank.service.PaymentService
import com.taw.polybank.service.RequestService
import com.taw.polybank.service.TransactionService
import com.taw.polybank.ui.companyFilters.ClientFilter
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

/**
 * @author Illya Rozumovskyy
 */
@Controller
@RequestMapping("/company/user")
class UserCompany(
  private val authorizedAccountService: AuthorizedAccountService,
  private val badgeService: BadgeService,
  private val bankAccountService: BankAccountService,
  private val beneficiaryService: BeneficiaryService,
  private val clientService: ClientService,
  private val companyService: CompanyService,
  private val currencyExchangeService: CurrencyExchangeService,
  private val employeeService: EmployeeService,
  private val paymentService: PaymentService,
  private val requestService: RequestService,
  private val transactionService: TransactionService,
) {
  @GetMapping("/")
  fun showUserHomepage(): String = "/company/userHomepage"

  @GetMapping("/blockedUser")
  fun blockedUserMenu(model: Model, session: HttpSession): String {
    val client = session.getAttribute("client") as ClientDTO
    model.addAttribute("message", "Your access have has been revoked.")

    val bankAccount = session.getAttribute("bankAccount") as BankAccountDTO
    val requests =
      requestService.findUnsolvedUnblockRequestByUserId(client.getId(), bankAccount.id)
    model.addAttribute("requests", requests)
    return "/company/blockedUser"
  }

  @PostMapping("/allegation")
  fun allegation(@RequestParam("msg") message: String?, session: HttpSession, model: Model): String {
    val client = session.getAttribute("client") as ClientDTO?
    val company = session.getAttribute("company") as CompanyDTO
    val bankAccount = company.getBankAccountByBankAccountId()

    val request = RequestDTO()

    request.setSolved(false)
    request.setTimestamp(Timestamp.from(Instant.now()))
    request.setType(RequestEntity.RequestType.ACTIVATION)
    request.setDescription(message)
    request.setApproved(false)
    request.setBankAccountByBankAccountId(bankAccount)
    val manager = employeeService.findManager()

    request.setEmployeeByEmployeeId(manager)
    request.setClientByClientId(client)

    requestService.save(request, clientService, bankAccountService, employeeService, badgeService)

    model.addAttribute(
      "message",
      "Allegation successfully submitted. Wait patiently for its resolution.",
    )

    return "redirect:/company/user/blockedUser"
  }

  @GetMapping("/logout")
  fun endSession(session: HttpSession): String {
    session.invalidate()
    return "redirect:/"
  }

  @GetMapping("/addRepresentative")
  fun addRepresentative(model: Model): String {
    val client = ClientDTO()
    client.setIsNew(true)
    model.addAttribute("client", client)
    return "/company/newRepresentative"
  }

  @PostMapping("/setUpPassword")
  fun setUpPassword(@ModelAttribute("client") client: ClientDTO?, model: Model): String {
    model.addAttribute("client", client)
    return "/company/setUpPassword"
  }

  @PostMapping("/saveNewPassword")
  fun saveNewPassword(
    @ModelAttribute("client") client: ClientDTO,
    @RequestParam("password") password: String?,
    session: HttpSession,
    model: Model,
  ): String {
    if (client.getIsNew()) {
      updateUser(client, password, session, model)
    } else {
      val oldClient = session.getAttribute("client") as ClientDTO
      client.setCreationDate(oldClient.getCreationDate())
      val passwordManager = PasswordManager(clientService)
      passwordManager.resetPassword(client, password)
      clientService.save(client)
    }

    return "/company/userHomepage"
  }

  @PostMapping("/saveRepresentative")
  fun save(@ModelAttribute("client") client: ClientDTO, session: HttpSession): String {
    val oldClient = session.getAttribute("client") as ClientDTO
    client.setCreationDate(oldClient.getCreationDate())
    session.setAttribute("client", client)
    clientService.save(client)
    return "/company/userHomepage"
  }

  private fun updateUser(client: ClientDTO, password: String?, session: HttpSession, model: Model) {
    model.addAttribute(
      "message",
      "User " + client.getName() + " " + client.getSurname() + " is successfully saved",
    )

    val company = session.getAttribute("company") as CompanyDTO
    val bankAccount = company.getBankAccountByBankAccountId()
    val authorizedAccount = AuthorizedAccountDTO()
    authorizedAccount.setClientByClientId(client)
    authorizedAccount.setBankAccountByBankAccountId(bankAccount)
    authorizedAccount.setBlocked(false)

    client.setCreationDate(Timestamp.from(Instant.now()))

    val passwordManager = PasswordManager(clientService)
    val saltAndPass = passwordManager.savePassword(client, password)
    clientService.save(client, saltAndPass)
    client.setIsNew(false)
    authorizedAccountService.save(
      authorizedAccount,
      clientService,
      bankAccountService,
      badgeService,
    )

    bankAccountService.addAuthorizedAccount(bankAccount, authorizedAccount)
  }

  @GetMapping("/editMyData")
  fun editMyData(session: HttpSession, model: Model): String {
    val client = session.getAttribute("client") as ClientDTO?
    model.addAttribute("client", client)
    return "/company/newRepresentative"
  }

  @GetMapping("/editCompanyData")
  fun editCompanyData(session: HttpSession, model: Model): String {
    model.addAttribute("company", session.getAttribute("company") as CompanyDTO?)
    return "/company/editCompanyData"
  }

  @PostMapping("/updateCompanyData")
  fun updateCompanyData(
    @ModelAttribute("company") company: CompanyDTO,
    session: HttpSession,
    model: Model,
  ): String {
    val oldCompany = session.getAttribute("company") as CompanyDTO
    oldCompany.setName(company.getName())
    companyService.save(oldCompany)
    model.addAttribute("message", "Company name was successfully changed to " + company.getName())
    return "/company/userHomepage"
  }

  @GetMapping("/listAllRepresentatives")
  fun listAllRepresentatives(model: Model, session: HttpSession): String = applyFilters(null, model, session)

  @PostMapping("/listFilteredRepresentatives")
  fun listFilteredRepresentatives(
    @ModelAttribute("clientFilter") clientFilter: ClientFilter?,
    model: Model,
    session: HttpSession,
  ): String = applyFilters(clientFilter, model, session)

  private fun applyFilters(clientFilter: ClientFilter?, model: Model, session: HttpSession): String {
    val company = session.getAttribute("company") as CompanyDTO
    val clientList: MutableList<ClientDTO?>?
    if (clientFilter == null) {
      model.addAttribute("clientFilter", ClientFilter())
      clientList = clientService.findAllRepresentativesOfGivenCompany(company.getId())
    } else {
      model.addAttribute("clientFilter", clientFilter)
      val registeredAfter = Timestamp(clientFilter.getRegisteredAfter().getTime())
      val registeredBefore = Timestamp(clientFilter.getRegisteredBefore().getTime())
      if (clientFilter.getNameOrSurname().isBlank()) {
        clientList =
          clientService.findAllRepresentativesOfACompanyThatWasRegisteredBetweenDates(
            company.getId(),
            registeredBefore,
            registeredAfter,
          )
      } else {
        clientList =
          clientService
            .findAllRepresentativesOfACompanyThatHasANameOrSurnameAndWasRegisteredBetweenDates(
              company.getId(),
              clientFilter.getNameOrSurname(),
              registeredBefore,
              registeredAfter,
            )
      }
    }
    model.addAttribute("clientList", clientList)
    model.addAttribute("clientService", clientService)
    model.addAttribute("authorizedAccountService", authorizedAccountService)
    return "/company/allRepresentatives"
  }

  @GetMapping("/blockRepresentative")
  fun blockRepresentative(@RequestParam("id") userId: Int?, session: HttpSession): String {
    val company = session.getAttribute("company") as CompanyDTO
    authorizedAccountService.findAndBlockAuthAccOfGivenClientAndCompany(userId, company.getId())
    return "redirect:/company/user/listAllRepresentatives"
  }

  @GetMapping("/newTransfer")
  fun newTransfer(): String = "/company/newTransfer"

  @PostMapping("/processTransfer")
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
      return "/company/userHomepage"
    }

    val company = session.getAttribute("company") as CompanyDTO
    val client = session.getAttribute("client") as ClientDTO?
    val bankAccount = company.getBankAccountByBankAccountId()
    if (bankAccount.balance < amount) {
      // fail message not enough money
      model.addAttribute(
        "message",
        "Money transfer was unsuccessful, not enough money in your bank account",
      )
      return "/company/userHomepage"
    }

    val originBadge = bankAccount.badgeByBadgeId
    var recipientBadge = BadgeDTO()
    val transaction = defineTransaction(client, bankAccount)
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
        if (clientRecipient!!.getName() != beneficiaryName) {
          // fail message name is not matching
          model.addAttribute(
            "message",
            "Money transfer was unsuccessful, recipient name is not correct",
          )
          return "/company/userHomepage"
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

    bankAccount.balance = bankAccount.balance - amount
    bankAccountService.save(bankAccount, clientService, badgeService)

    // success message
    model.addAttribute(
      "message",
      (
        amount
          .toString() + " " +
          bankAccount.badgeByBadgeId.getName() +
          " was successfully transferred to " +
          beneficiaryName
        ),
    )
    return "/company/userHomepage"
  }

  @GetMapping("/moneyExchange")
  fun moneyExchange(model: Model): String {
    val badge = BadgeDTO()
    val badgeList = badgeService.findAll()

    model.addAttribute("badge", badge)
    model.addAttribute("badgeList", badgeList)
    return "/company/moneyExchange"
  }

  @PostMapping("/makeExchange")
  fun makeExchange(
    @ModelAttribute targetBadge: BadgeDTO,
    session: HttpSession,
    model: Model,
  ): String {
    var targetBadge = targetBadge
    val company = session.getAttribute("company") as CompanyDTO
    val client = session.getAttribute("client") as ClientDTO?
    val bankAccount = company.getBankAccountByBankAccountId()
    val currentBadge = bankAccount.badgeByBadgeId
    targetBadge = badgeService.findById(targetBadge.getId())
    val transaction = defineTransaction(client, bankAccount)

    val beneficiary =
      defineBeneficiary(company.getName(), bankAccount.iban, targetBadge)
    val payment = definePayment(bankAccount.balance, beneficiary)

    payment.setBenficiaryByBenficiaryId(beneficiary)

    if (currentBadge.getId() != targetBadge.getId()) {
      val currencyExchange =
        defineCurrencyExchange(
          currentBadge,
          targetBadge,
          bankAccount.balance,
          transaction,
          payment,
        )
      transaction.setPaymentByPaymentId(payment)
      bankAccount.balance = currencyExchange.getFinalAmount()
      bankAccount.badgeByBadgeId = targetBadge
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
      bankAccountService.save(bankAccount, clientService, badgeService)
      model.addAttribute(
        "message",
        (
          currencyExchange.getInitialAmount()
            .toString() + " " +
            currentBadge.getName() +
            " was successfully exchanged to " +
            currencyExchange.getFinalAmount() +
            " " +
            targetBadge.getName()
          ),
      )
    } else {
      model.addAttribute(
        "message",
        "No exchange was made, chosen currency is actual currency of your bank account.",
      )
    }
    return "/company/userHomepage"
  }

  private fun definePayment(amount: Double, beneficiary: BenficiaryDTO?): PaymentDTO {
    val payment = PaymentDTO()
    payment.setAmount(amount)
    payment.setBenficiaryByBenficiaryId(beneficiary)
    return payment
  }

  @GetMapping("/operationHistory")
  fun operationHistory(session: HttpSession, model: Model): String = operationHistoryFilters(null, session, model)

  @PostMapping("/operationHistory")
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
    val company = session.getAttribute("company") as CompanyDTO
    val bankAccount = company.getBankAccountByBankAccountId()

    if (transactionFilter == null) {
      transactionList =
        transactionService.findTransactionsByBankAccountByBankAccountIdId(bankAccount.id)
      transactionFilter = TransactionFilterIllya()
    } else {
      val dateAfter = Timestamp(transactionFilter.getTransactionAfter().getTime())
      val dateBefore = Timestamp(transactionFilter.getTransactionBefore().getTime())
      if (transactionFilter.getSenderId().isBlank() &&
        transactionFilter.getRecipientName().isBlank()
      ) {
        transactionList =
          transactionService.findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
            bankAccount.id,
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
              bankAccount.id,
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
              bankAccount.id,
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
              bankAccount.id,
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
    return "/company/operationHistory"
  }

  private fun defineTransaction(client: ClientDTO?, bankAccount: BankAccountDTO?): TransactionDTO {
    val transaction = TransactionDTO()
    transaction.setTimestamp(Timestamp.from(Instant.now()))
    transaction.setClientByClientId(client)
    transaction.setBankAccountByBankAccountId(bankAccount)
    return transaction
  }

  private fun updateBadges(
    originBadge: BadgeDTO?,
    recipientBadge: BadgeDTO?,
    currencyExchange: CurrencyExchangeDTO?,
  ) {
    badgeService.addAndSave(originBadge, currencyExchange)
    badgeService.addAndSave(recipientBadge, currencyExchange)
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
    // updateBadges(originBadge, recipientBadge, currencyExchange);
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
