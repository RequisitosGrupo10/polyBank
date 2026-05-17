package com.taw.polybank.controller

import com.taw.polybank.dao.EmployeeRepository
import com.taw.polybank.entity.EmployeeEntity
import com.taw.polybank.service.BankAccountService
import com.taw.polybank.service.ClientService
import com.taw.polybank.service.CompanyService
import com.taw.polybank.service.EmployeeService
import com.taw.polybank.service.TransactionService
import com.taw.polybank.ui.client.ClientFilter
import com.taw.polybank.ui.company.CompanyFilter
import com.taw.polybank.ui.transaction.TransactionFilterJose
import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author José Manuel Sánchez Rico
 */
@Controller
@RequestMapping("/employee")
class EmployeeController {
    @Autowired
    private val employeeRepository: EmployeeRepository? = null

    @Autowired
    private val employeeService: EmployeeService? = null

    @Autowired
    private val companyService: CompanyService? = null

    @Autowired
    private val clientService: ClientService? = null

    @Autowired
    private val bankAccountService: BankAccountService? = null

    @Autowired
    private val transactionService: TransactionService? = null

    @GetMapping(value = ["", "/", "/login", "/login/"])
    fun doBase(): String = ("employee/login")

    @PostMapping("/login")
    fun postLogin(
        @RequestParam("dni") dni: String,
        session: HttpSession,
    ): String {
        if (dni.isBlank()) return "redirect:/employee"
        val employeeOpt = employeeRepository!!.findByDNI(dni)
        if (employeeOpt.isPresent()) {
            val employee = employeeOpt.get()
            session.setAttribute("employee", employee)
            if (employee.getType().toString() == "assistant") {
                return ("redirect:/employee/assistance")
            } else if (employee.getType().toString() == "manager") {
                return ("redirect:/employee/manager")
            }
            session.setAttribute("employee", employee)
        }
        return ("redirect:/employee/")
    }

    @GetMapping(value = ["manager", "manager/"])
    fun getActions(
        model: Model?,
        session: HttpSession,
    ): String {
        if (session.getAttribute("employee") == null) return "redirect:/employee"
        return ("employee/manager/actions")
    }

    @GetMapping("manager/requests")
    fun getRequests(
        session: HttpSession,
        model: Model,
    ): String {
        if (session.getAttribute("employee") == null) return ("redirect:/employee")
        val requestDTOS = employeeService!!.findRequestsForEmployee(session.getAttribute("employee") as EmployeeEntity?)
        model.addAttribute("requests", requestDTOS)
        return ("employee/manager/requests")
    }

    @GetMapping("manager/accounts/clients")
    fun getClientAccounts(model: Model): String {
        model.addAttribute("clients", clientService!!.findAll())
        model.addAttribute("filtro", ClientFilter())
        return ("employee/manager/client_accounts")
    }

    @PostMapping("manager/accounts/clients")
    fun postClientAccounts(
        model: Model,
        @ModelAttribute("filtro") filter: ClientFilter?,
    ): String {
        model.addAttribute("clients", clientService!!.findByFilter(filter))
        if (filter == null) model.addAttribute("filtro", ClientFilter())
        return ("employee/manager/client_accounts")
    }

    @GetMapping("manager/accounts/companies")
    fun getCompanyAccounts(model: Model): String {
        model.addAttribute("companies", companyService!!.findAll())
        model.addAttribute("filtro", ClientFilter())
        return ("employee/manager/company_accounts")
    }

    @PostMapping("manager/accounts/companies")
    fun postCompanyAccounts(
        model: Model,
        @ModelAttribute("filtro") companyFilter: CompanyFilter?,
    ): String {
        model.addAttribute("companies", companyService!!.findByFilter(companyFilter))
        if (companyFilter == null) model.addAttribute("filtro", CompanyFilter())
        return ("employee/manager/company_accounts")
    }

    @GetMapping("manager/approve/{id}")
    fun getApprove(
        @PathVariable("id") id: Int?,
        model: Model?,
    ): String {
        employeeService!!.solveRequest(id, true)
        return ("redirect:/employee/manager/requests")
    }

    @GetMapping("manager/deny/{id}")
    fun getDeny(
        @PathVariable("id") id: Int?,
        model: Model?,
    ): String {
        employeeService!!.solveRequest(id, false)
        return ("redirect:/employee/manager/requests")
    }

    @GetMapping("manager/account/client/{id}")
    fun getClientAccount(
        @PathVariable("id") id: Int,
        model: Model,
    ): String {
        val clientDTOOptional = clientService!!.findById(id)
        if (clientDTOOptional.isEmpty()) return ("redirect:/employee/manager/accounts/clients")
        model.addAttribute("client", clientDTOOptional.get())
        model.addAttribute("filtro", TransactionFilterJose())
        model.addAttribute("transactions", transactionService!!.findByClientId(id))
        return ("employee/manager/see_client_account")
    }

    @PostMapping("manager/account/client/{id}")
    fun postClientAccount(
        @PathVariable("id") id: Int?,
        @ModelAttribute("filtro") filter: TransactionFilterJose,
        model: Model,
    ): String {
        val clientDTOOptional = clientService!!.findById(id)
        if (clientDTOOptional.isEmpty()) return ("redirect:/employee/manager/accounts/clients")
        model.addAttribute("client", clientDTOOptional.get())
        model.addAttribute("transactions", transactionService!!.findByClientIdAndFilter(id, filter))
        return ("employee/manager/see_client_account")
    }

    @GetMapping("manager/account/company/{id}")
    fun getCompanyAccount(
        @PathVariable("id") id: Int?,
        model: Model,
    ): String {
        val companyDTOOptional = companyService!!.findById(id)
        if (companyDTOOptional.isEmpty()) return ("redirect:/employee/manager/accounts/companies")
        model.addAttribute("company", companyDTOOptional.get())
        model.addAttribute("filtro", TransactionFilterJose())
        model.addAttribute(
            "transactions",
            transactionService!!.findByBankId(companyDTOOptional.get().getBankAccountByBankAccountId().id),
        )
        return ("employee/manager/see_company_account")
    }

    @PostMapping("manager/account/company/{id}")
    fun postCompanyAccount(
        @PathVariable("id") id: Int?,
        @ModelAttribute("filtro") filter: TransactionFilterJose,
        model: Model,
    ): String {
        val companyDTOOptional = companyService!!.findById(id)
        if (companyDTOOptional.isEmpty()) return ("redirect:/employee/manager/accounts/companies")
        val companyDTO = companyDTOOptional.get()
        model.addAttribute("company", companyDTO)
        model.addAttribute(
            "transactions",
            transactionService!!
                .findByBankIdAndFilter(companyDTO.getBankAccountByBankAccountId().id, filter),
        )
        return ("employee/manager/see_company_account")
    }

    @GetMapping("manager/suspicious")
    fun getSuspicious(model: Model): String {
        model.addAttribute("suspicious", bankAccountService!!.findSuspicious())
        return ("employee/manager/suspicious")
    }

    @GetMapping("manager/block/account/{id}")
    fun getBlocked(
        @PathVariable("id") id: Int?,
    ): String {
        bankAccountService!!.blockAccountById(id)
        return ("redirect:/employee/manager/suspicious")
    }

    @GetMapping("manager/disable/account/{id}")
    fun getDisabled(
        @PathVariable("id") id: Int?,
    ): String {
        bankAccountService!!.blockAccountById(id)
        return ("redirect:/employee/manager/accounts/inactive")
    }

    @GetMapping("manager/accounts/inactive")
    fun getInactiveAccounts(model: Model): String {
        val bankAccountDTOS = bankAccountService!!.findInactive()
        model.addAttribute("inactive", bankAccountDTOS)
        return ("employee/manager/inactive_accounts")
    }
}
