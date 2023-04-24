package com.taw.polybank.controller;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.CompanyRepository;
import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dao.RequestRepository;
import com.taw.polybank.dto.CompanyDTO;
import com.taw.polybank.dto.EmployeeDTO;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.EmployeeEntity;
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

    @GetMapping("")
    public String doBase()
    {
        return ("employee/login");
    }

    @GetMapping("/login")
    public String getLogin()
    {
        return doBase();
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam("dni") String dni, @RequestParam("password") String password,
                            HttpSession session)
    {
        Optional<EmployeeEntity> employee_opt = employeeRepository.findByDNI(dni);
        if (employee_opt.isPresent()) {
            EmployeeEntity employee = employee_opt.get();
            // It should be validated : BCrypt.checkpw(password + employee.getSalt(), employee.getPassword())
            session.setAttribute("employeeID", employee.getId());
            session.setAttribute("employeeType", employee.getType());
        }
        return ("redirect:/employee/");
    }

    @GetMapping("manager/requests")
    public String getRequests(Model model) {
        model.addAttribute("requests", requestRepository.findAll());
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
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("companies", companyService.findAll());
        return ("employee/manager/inactive_accounts");
    }

    @GetMapping("manager/approve/{id}")
    public String getApprove(@PathVariable("id") Integer id, Model model) {
        employeeService.solveRequest(id, (byte) 1);
        return ("redirect:/employee/manager/requests");
    }

    @GetMapping("manager/deny/{id}")
    public String getDeny(@PathVariable("id") Integer id, Model model) {
        employeeService.solveRequest(id, (byte) 0);
        return ("redirect:/employee/manager/requests");
    }

    @GetMapping("manager/accounts/company/{id}")
    public String getCompanyAccounts(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("companies", companyService.findById(id));
        return ("employee/manager/see_company_account");
    }

    @GetMapping("manager/suspicious")
    public String getSuspicious(Model model) {

        return ("employee/manager/suspicious");
    }

    @GetMapping("/register")
    public String getRegister(Model model)
    {
        model.addAttribute("newEmployee", new EmployeeDTO());
        return ("employee/register");
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute("newEmployee") EmployeeDTO employee)
    {
        return ("redirect:/employee/");
    }
}
