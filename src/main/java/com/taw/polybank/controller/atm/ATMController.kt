package com.taw.polybank.controller.atm

import com.taw.polybank.dto.BadgeDTO
import com.taw.polybank.dto.BankAccountDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.entity.RequestEntity
import com.taw.polybank.service.BadgeService
import com.taw.polybank.service.BankAccountService
import com.taw.polybank.service.BeneficiaryService
import com.taw.polybank.service.ClientService
import com.taw.polybank.service.RequestService
import com.taw.polybank.service.SuspiciousAccountService
import com.taw.polybank.service.TransactionService
import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.sql.Date
import java.time.LocalDate

/**
 * @author Lucía Gutiérrez Molina
 */
@Controller
@RequestMapping("/atm")
class ATMController {
    @Autowired
    private val bankAccountService: BankAccountService? = null

    @Autowired
    private val clientService: ClientService? = null

    @Autowired
    private val beneficiaryService: BeneficiaryService? = null

    @Autowired
    private val badgeService: BadgeService? = null

    @Autowired
    private val transactionService: TransactionService? = null

    @Autowired
    private val requestService: RequestService? = null

    @Autowired
    private val suspiciousAccountService: SuspiciousAccountService? = null

    @GetMapping("/")
    fun doIndex(
        model: Model,
        session: HttpSession,
    ): String {
        val client = session.getAttribute("client") as ClientDTO?
        if (client == null) {
            return "atm/index"
        } else {
            val bankAccounts = bankAccountService!!.findByClient(client)
            model.addAttribute("bankAccounts", bankAccounts)
            return "atm/user_data"
        }
    }

    @PostMapping("/login")
    fun doMostrarDatos(
        @RequestParam("userName") user: String?,
        @RequestParam("password") password: String?,
        model: Model,
        session: HttpSession,
    ): String {
        val client = clientService!!.autenticar(user, password)
        if (client == null) {
            model.addAttribute("error", "User with given ID and password is not found")
            return "atm/index"
        } else {
            session.setAttribute("client", client)
        }
        return "redirect:/atm/"
    }

    @GetMapping("/editarDatos")
    fun gotoEditarDatos(
        model: Model,
        session: HttpSession,
    ): String {
        val client = session.getAttribute("client") as ClientDTO?
        if (client == null) {
            return "atm/index"
        } else {
            model.addAttribute("client", client)
            return "atm/user_edit"
        }
    }

    @PostMapping("/editarDatos")
    fun doEditarDatos(
        @ModelAttribute("client") client: ClientDTO,
        model: Model,
        session: HttpSession,
    ): String {
        if (session.getAttribute("client") == null) return "atm/index"
        clientService!!.guardarCliente(client, "")
        beneficiaryService!!.guardarBeneficiarios(client)
        session.setAttribute("client", client)
        model.addAttribute("bankAccounts", bankAccountService!!.findByClient(client))
        return "atm/user_data"
    }

    @PostMapping("/changePassword")
    fun doChangePassword(
        @RequestParam("password") password: String,
        @RequestParam("password2") password2: String?,
        model: Model,
        session: HttpSession,
    ): String {
        val client = session.getAttribute("client") as ClientDTO?
        if (client == null) return "atm/index"
        if (password != password2) {
            model.addAttribute("error", "Contraseñas no coinciden")
            model.addAttribute("client", client)
            return "atm/user_edit"
        } else if (password.length < 7) {
            model.addAttribute("error", "La contraseña es muy corta. Debe tener más de 7 símbolos.")
            model.addAttribute("client", client)
            return "atm/user_edit"
        }
        clientService!!.guardarCliente(client, password)
        session.setAttribute("client", client)
        model.addAttribute("bankAccounts", bankAccountService!!.findByClient(client))
        return "atm/user_data"
    }

    @PostMapping("/enumerarAcciones")
    fun doListarAcciones(
        @RequestParam(name = "bankAccount", required = false) bankAccountId: Int?,
        session: HttpSession,
    ): String {
        val client = session.getAttribute("client") as ClientDTO?
        if (client == null) return "atm/index"

        if (bankAccountId != null) {
            val bankAccount = bankAccountService!!.findById(bankAccountId)
            session.setAttribute("bankAccount", bankAccount)
            val badge = badgeService!!.findByBankAccountsById(bankAccount)
            session.setAttribute("badge", badge)
        }

        return "atm/bankAccount_actions"
    }

    @GetMapping("/makeTransfer")
    fun doMenuTransfer(session: HttpSession): String {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null) return "atm/index"
        return "atm/bankAccount_transferMenu"
    }

    @PostMapping("/makeTransfer")
    fun doMakeTransfer(
        @RequestParam("amount") amount: Double,
        @RequestParam("receiver") receiverIBAN: String?,
        @RequestParam("receiverName") receiverName: String?,
        model: Model,
        session: HttpSession,
    ): String {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null) return "atm/index"

        val bankAccountReceiver = bankAccountService!!.findByIban(receiverIBAN)

        if (suspiciousAccountService!!.isSuspicious(receiverIBAN)) {
            model.addAttribute(
                "error",
                "The destination account is suspicious. You cannot transfer money to it.",
            )
            return "atm/bankAccount_transferMenu"
        }
        if (bankAccountReceiver != null &&
            bankAccountReceiver.clientByClientId!!.getName() != receiverName
        ) {
            model.addAttribute(
                "error",
                "The name of the proprietary of the destination account is not correct.",
            )
            return "atm/bankAccount_transferMenu"
        }

        val emisorBadge = session.getAttribute("badge") as BadgeDTO
        val badgeReceiver: BadgeDTO
        if (bankAccountReceiver != null) {
            badgeReceiver = badgeService!!.findByBankAccountsById(bankAccountReceiver)
        } else {
            badgeReceiver = session.getAttribute("badge") as BadgeDTO
        }

        val bankAccountEmisor = session.getAttribute("bankAccount") as BankAccountDTO
        val client = session.getAttribute("client") as ClientDTO

        transactionService!!.makeTransaction(
            amount,
            receiverIBAN,
            receiverName,
            badgeReceiver,
            emisorBadge,
            bankAccountEmisor,
            client,
        )

        val bankAccountDTO = transactionService.updateBankAccount(bankAccountEmisor)
        session.setAttribute("bankAccount", bankAccountDTO)

        return "atm/bankAccount_actions"
    }

    @GetMapping("/takeOut")
    fun menuTakeOut(
        model: Model,
        session: HttpSession,
    ): String {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null) return "atm/index"

        val badges = badgeService!!.findAllBadges()
        model.addAttribute("badges", badges)

        return "atm/bankAccount_takeOut"
    }

    @PostMapping("/takeOut")
    fun doTakeOut(
        @RequestParam("amount") amount: Double,
        @RequestParam("badge") badgeId: Int,
        session: HttpSession,
    ): String {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null) return "atm/index"

        val bankAccount = session.getAttribute("bankAccount") as BankAccountDTO
        val client = session.getAttribute("client") as ClientDTO
        val emisorBadge = session.getAttribute("badge") as BadgeDTO
        val badge = badgeService!!.findById(badgeId)

        transactionService!!.makeTransaction(
            amount,
            bankAccount.iban,
            client.getName(),
            badge,
            emisorBadge,
            bankAccount,
            client,
        )

        val bankAccountDTO = transactionService.updateBankAccount(bankAccount)
        session.setAttribute("bankAccount", bankAccountDTO)

        return "atm/bankAccount_actions"
    }

    @GetMapping("/checkTransactions")
    fun listTransactions(
        session: HttpSession,
        model: Model,
    ): String {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null) return "atm/index"

        val bankAccount = session.getAttribute("bankAccount") as BankAccountDTO
        val transactions =
            transactionService!!.findByBankAccountByBankAccountId(bankAccount)
        val filter =
            TransactionFilterLucia(
                Date.valueOf(LocalDate.now()),
                Date.valueOf(LocalDate.now()),
                "",
                "",
                "0.0",
            )

        model.addAttribute("transactions", transactions)
        model.addAttribute("filter", filter)
        return "atm/bankAccount_transactions"
    }

    @PostMapping("/checkTransactions")
    fun filterTransactions(
        session: HttpSession,
        model: Model,
        @ModelAttribute("filter") filter: TransactionFilterLucia,
    ): String {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null) return "atm/index"

        val bankAccount = session.getAttribute("bankAccount") as BankAccountDTO
        val transactions = transactionService!!.filter(bankAccount, filter)

        model.addAttribute("transactions", transactions)
        return "atm/bankAccount_transactions"
    }

    @GetMapping("/requestUnban")
    fun doRequestUnban(
        session: HttpSession,
        model: Model,
    ): String {
        val client = session.getAttribute("client") as ClientDTO?
        val bankAccount = session.getAttribute("bankAccount") as BankAccountDTO?
        if (client == null || bankAccount == null) {
            return "atm/index"
        }

        val requestsNotSolved =
            requestService!!.findByBankAccountByBankAccountIdAndAndSolved(bankAccount, false)

        if (requestsNotSolved.size == 0) {
            return "atm/requestUnban"
        }

        model.addAttribute("requests", requestsNotSolved)

        return "atm/showRequest"
    }

    @GetMapping("/makeUnbanPetition")
    fun doMakeUnbanPetition(
        session: HttpSession,
        @RequestParam("description") description: String?,
    ): String {
        val client = session.getAttribute("client") as ClientDTO?
        val bankAccount = session.getAttribute("bankAccount") as BankAccountDTO?
        if (client == null || bankAccount == null) {
            return "atm/index"
        }

        requestService!!.createNewRequest(client, bankAccount, RequestEntity.RequestType.ACTIVATION, description)

        return "redirect:/atm/requestUnban"
    }
}
