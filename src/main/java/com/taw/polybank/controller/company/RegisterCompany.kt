package com.taw.polybank.controller.company

import com.taw.polybank.controller.PasswordManager
import com.taw.polybank.dto.BankAccountDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.dto.CompanyDTO
import com.taw.polybank.dto.RequestDTO
import com.taw.polybank.entity.RequestEntity
import com.taw.polybank.service.BadgeService
import com.taw.polybank.service.BankAccountService
import com.taw.polybank.service.ClientService
import com.taw.polybank.service.CompanyService
import com.taw.polybank.service.EmployeeService
import com.taw.polybank.service.RequestService
import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Autowired
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
 * @author Illya Rozumovskyy
 */
@Controller
@RequestMapping("/company")
class RegisterCompany {
    @Autowired
    protected var badgeService: BadgeService? = null

    @Autowired
    protected var bankAccountService: BankAccountService? = null

    @Autowired
    protected var clientService: ClientService? = null

    @Autowired
    protected var companyService: CompanyService? = null

    @Autowired
    protected var requestService: RequestService? = null

    @Autowired
    protected var employeeService: EmployeeService? = null

    @GetMapping("/registerCompany")
    fun doRegister(model: Model): String {
        val badgeList = badgeService!!.findAll()
        model.addAttribute("badgeList", badgeList)

        return "/company/registerCompany"
    }

    @PostMapping("/registerCompanyOwner")
    fun doRegisterCompanyOwner(
        @RequestParam("name") companyName: String?,
        @RequestParam("badge") badgeId: Int?,
        model: Model,
        session: HttpSession,
    ): String {
        val client = ClientDTO()
        model.addAttribute("client", client)

        val company = CompanyDTO()
        company.setName(companyName)

        val badge = badgeService!!.findById(badgeId!!)
        val bankAccount = BankAccountDTO()
        bankAccount.badgeByBadgeId = badge
        company.setBankAccountByBankAccountId(bankAccount)
        session.setAttribute("bankAccount", company.getBankAccountByBankAccountId())
        session.setAttribute("company", company)
        return "/company/registerOwner"
    }

    @PostMapping("/saveNewCompany")
    fun doSaveNewCompany(
        @ModelAttribute("client") client: ClientDTO,
        @RequestParam("password") password: String?,
        model: Model?,
        session: HttpSession,
    ): String {
        val bankAccount = session.getAttribute("bankAccount") as BankAccountDTO
        val company = session.getAttribute("company") as CompanyDTO?
        val request = RequestDTO()
        updateBankAccount(bankAccount)
        // filling up bank account fields
        bankAccount.clientByClientId = client

        // filling up Client fields
        client.setCreationDate(Timestamp.from(Instant.now()))

        val passwordManager = PasswordManager(clientService!!)
        val saltAndPass = passwordManager.savePassword(client, password)

        // creating activation request
        defineActivationRequest(client, bankAccount, request)

        // saving DTOs
        clientService!!.save(client, saltAndPass)
        companyService!!.save(company, bankAccountService, clientService, badgeService)
        bankAccount.id = bankAccountService!!.getBankAccountId(bankAccount)
        requestService!!.save(request, clientService, bankAccountService, employeeService, badgeService)

        session.invalidate()
        return "redirect:/"
    }

    private fun updateBankAccount(bankAccount: BankAccountDTO) {
        bankAccount.isActive = false
        val random = Random()
        val iban = StringBuilder()
        iban.append("ES44 5268 3000 ")
        for (i in 0..11) {
            iban.append(random.nextInt(10))
        }
        bankAccount.balance = 0.0
        bankAccount.iban = iban.toString()
    }

    private fun defineActivationRequest(
        client: ClientDTO?,
        bankAccount: BankAccountDTO?,
        request: RequestDTO,
    ) {
        request.setSolved(false)
        request.setTimestamp(Timestamp.from(Instant.now()))
        request.setType(RequestEntity.RequestType.ACTIVATION)
        request.setDescription("Activate company bank Account")
        request.setApproved(false)
        request.setBankAccountByBankAccountId(bankAccount)

        val manager = employeeService!!.findManager()
        request.setEmployeeByEmployeeId(manager)
        request.setClientByClientId(client)
    }
}
