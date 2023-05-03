package com.taw.polybank.controller;

import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.EmployeeEntity;
import com.taw.polybank.service.*;
import com.taw.polybank.ui.client.ClientFilter;
import com.taw.polybank.ui.company.CompanyFilter;
import com.taw.polybank.ui.transaction.TransactionFilterJose;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = {"", "/", "/login", "/login/"})
    public String doBase()
    {
        return ("employee/login");
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam("dni") String dni,
                            HttpSession session)
    {
        if (dni.isBlank())
            return "redirect:/employee";
        Optional<EmployeeEntity> employee_opt = employeeRepository.findByDNI(dni);
        if (employee_opt.isPresent()) {
            EmployeeEntity employee = employee_opt.get();
            session.setAttribute("employee", employee);
            if (employee.getType().toString().equals("assistant"))
                return ("redirect:/employee/assistance");
            else if (employee.getType().toString().equals("manager") )
                return ("redirect:/employee/manager");
            session.setAttribute("employee", employee);
        }
        return ("redirect:/employee/");
    }

    @GetMapping(value={"manager", "manager/"})
    public String getActions(Model model, HttpSession session) {
        if (session.getAttribute("employee") == null)
            return "redirect:/employee";
        return ("employee/manager/actions");
    }

    @GetMapping("manager/requests")
    public String getRequests(HttpSession session, Model model) {
        if (session.getAttribute("employee") == null)
            return ("redirect:/employee");
        List<RequestDTO> requestDTOS = employeeService.findRequestsForEmployee((EmployeeEntity) session.getAttribute("employee"));
        model.addAttribute("requests", requestDTOS);
        return ("employee/manager/requests");
    }

    @GetMapping("manager/accounts/clients")
    public String getClientAccounts(Model model) {
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("filtro", new ClientFilter());
        return ("employee/manager/client_accounts");
    }

    @PostMapping("manager/accounts/clients")
    public String postClientAccounts(Model model,
                                     @ModelAttribute("filtro") ClientFilter filter) {
        model.addAttribute("clients", clientService.findByFilter(filter));
        if (filter == null)
            model.addAttribute("filtro", new ClientFilter());
        return ("employee/manager/client_accounts");
    }

    @GetMapping("manager/accounts/companies")
    public String getCompanyAccounts(Model model) {
        model.addAttribute("companies", companyService.findAll());
        model.addAttribute("filtro", new ClientFilter());
        return ("employee/manager/company_accounts");
    }

    @PostMapping("manager/accounts/companies")
    public String postCompanyAccounts(Model model,
                                     @ModelAttribute("filtro")CompanyFilter companyFilter) {
        model.addAttribute("companies", companyService.findByFilter(companyFilter));
        if (companyFilter == null)
            model.addAttribute("filtro", new CompanyFilter());
        return ("employee/manager/company_accounts");
    }

    @GetMapping("manager/approve/{id}")
    public String getApprove(@PathVariable("id") Integer id, Model model) {
        employeeService.solveRequest(id, true);
        return ("redirect:/employee/manager/requests");
    }

    @GetMapping("manager/deny/{id}")
    public String getDeny(@PathVariable("id") Integer id, Model model) {
        employeeService.solveRequest(id, false);
        return ("redirect:/employee/manager/requests");
    }

    @GetMapping("manager/account/client/{id}")
    public String getClientAccount(@PathVariable("id") Integer id, Model model) {
        Optional<ClientDTO> clientDTOOptional = clientService.findById(id);
        if (clientDTOOptional.isEmpty())
            return ("redirect:/employee/manager/accounts/clients");
        model.addAttribute("client", clientDTOOptional.get());
        model.addAttribute("filtro", new TransactionFilterJose());
        model.addAttribute("transactions", transactionService.findByClientId(id));
        return ("employee/manager/see_client_account");
    }

    @PostMapping("manager/account/client/{id}")
    public String postClientAccount(@PathVariable("id") Integer id,
                                   @ModelAttribute("filtro") TransactionFilterJose filter,
                                   Model model) {
        Optional<ClientDTO> clientDTOOptional = clientService.findById(id);
        if (clientDTOOptional.isEmpty())
            return ("redirect:/employee/manager/accounts/clients");
        model.addAttribute("client", clientDTOOptional.get());
        model.addAttribute("transactions", transactionService.findByClientIdAndFilter(id, filter));
        return ("employee/manager/see_client_account");
    }


    @GetMapping("manager/account/company/{id}")
    public String getCompanyAccount(@PathVariable("id") Integer id, Model model) {
        Optional<CompanyDTO> companyDTOOptional = companyService.findById(id);
        if (companyDTOOptional.isEmpty())
            return ("redirect:/employee/manager/accounts/companies");
        model.addAttribute("company", companyDTOOptional.get());
        model.addAttribute("filtro", new TransactionFilterJose());
        model.addAttribute("transactions", transactionService.findByBankId(companyDTOOptional.get().getBankAccountByBankAccountId().getId()));
        return ("employee/manager/see_company_account");
    }

    @PostMapping("manager/account/company/{id}")
    public String postCompanyAccount(@PathVariable("id") Integer id,
                                     @ModelAttribute("filtro") TransactionFilterJose filter,
                                     Model model) {
        Optional<CompanyDTO> companyDTOOptional = companyService.findById(id);
        if (companyDTOOptional.isEmpty())
            return ("redirect:/employee/manager/accounts/companies");
        CompanyDTO companyDTO = companyDTOOptional.get();
        model.addAttribute("company", companyDTO);
        model.addAttribute("transactions", transactionService
                        .findByBankIdAndFilter(companyDTO.getBankAccountByBankAccountId().getId(), filter));
        return ("employee/manager/see_company_account");
    }


    @GetMapping("manager/suspicious")
    public String getSuspicious(Model model) {
        model.addAttribute("suspicious", bankAccountService.findSuspicious());
        return ("employee/manager/suspicious");
    }

    @GetMapping("manager/block/account/{id}")
    public String getDisabled(@PathVariable("id") Integer id) {
        bankAccountService.blockAccountById(id);
        return ("redirect:/employee/manager/suspicious");
    }

    @GetMapping("manager/disable/account/{id}")
    public String getBlocked(@PathVariable("id") Integer id) {
        bankAccountService.blockAccountById(id);
        return ("redirect:/employee/manager/accounts/inactive");
    }


    @GetMapping("manager/accounts/inactive")
    public String getInactiveAccounts(Model model) {
        List<BankAccountDTO> bankAccountDTOS = bankAccountService.findInactive();
        model.addAttribute("inactive", bankAccountDTOS);
        return ("employee/manager/inactive_accounts");
    }
}
