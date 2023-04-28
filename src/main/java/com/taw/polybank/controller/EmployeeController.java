package com.taw.polybank.controller;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.CompanyRepository;
import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dao.RequestRepository;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.dto.CompanyDTO;
import com.taw.polybank.dto.EmployeeDTO;
import com.taw.polybank.dto.RequestDTO;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.EmployeeEntity;
import com.taw.polybank.service.BankAccountService;
import com.taw.polybank.service.ClientService;
import com.taw.polybank.service.CompanyService;
import com.taw.polybank.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping(value = {"", "/", "/login", "/login/"})
    public String doBase()
    {
        return ("employee/login");
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam("dni") String dni, @RequestParam("password") String password,
                            HttpSession session)
    {
        if (dni.isBlank())
            return "redirect:/employee";
        Optional<EmployeeEntity> employee_opt = employeeRepository.findByDNI(dni);
        if (employee_opt.isPresent()) {
            EmployeeEntity employee = employee_opt.get();
            // It should be validated : BCrypt.checkpw(password + employee.getSalt(), employee.getPassword())
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
        List<ClientEntity> clientEntityList;
        model.addAttribute("clients", clientService.findAll());
        return ("employee/manager/client_accounts");
    }

    @GetMapping("manager/accounts/companies")
    public String getCompanyAccounts(Model model) {
        List<CompanyDTO> companyDTOList = companyService.findAll();
        model.addAttribute("companies", companyDTOList);
        return ("employee/manager/company_accounts");
    }

    @GetMapping("manager/accounts/inactive")
    public String getInactiveAccounts(Model model) {
        model.addAttribute("inactive", bankAccountService.findInactive());
        return ("employee/manager/inactive_accounts");
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

    @GetMapping("manager/accounts/client/{id}")
    public String getClientAccount(@PathVariable("id") Integer id, Model model) {
        Optional<ClientDTO> clientDTOOptional = clientService.findById(id);
        if (clientDTOOptional.isEmpty())
            return ("redirect:/employee/manager/accounts/clients");
        model.addAttribute("client", clientDTOOptional.get());
        return ("employee/manager/see_client_account");
    }

    @GetMapping("manager/account/company/{id}")
    public String getCompanyAccount(@PathVariable("id") Integer id, Model model) {
        Optional<CompanyDTO> companyDTOOptional = companyService.findById(id);
        if (companyDTOOptional.isEmpty())
            return ("redirect:/employee/manager/accounts/companies");
        model.addAttribute("company", companyDTOOptional.get());
        return ("employee/manager/see_company_account");
    }

    @GetMapping("manager/suspicious")
    public String getSuspicious(Model model) {
        model.addAttribute("suspicious", bankAccountService.findSuspicious());
        return ("employee/manager/suspicious");
    }
}
