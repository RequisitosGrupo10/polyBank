package com.taw.polybank.controller;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.CompanyRepository;
import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dao.RequestRepository;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.EmployeeEntity;
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

    @GetMapping("/")
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
    public String postLogin(@RequestParam("DNI") String dni, @RequestParam("password") String password,
                            HttpSession session)
    {
        Optional<EmployeeEntity> employee_opt = employeeRepository.findByDNI(dni);
        if (employee_opt.isPresent()) {
            EmployeeEntity employee = employee_opt.get();
            // It should be validated : BCrypt.checkpw(password + employee.getSalt(), employee.getPassword())
            session.setAttribute("employeeID", employee.getId());
            session.setAttribute("type", employee.getType());
            return ("employee/actions");
        }
        return ("redirect:/employee/");
    }

    @GetMapping("/requests")
    public String getRequests(Model model) {
        model.addAttribute("requests", requestRepository.findAll());
        return ("employee/requests");
    }

    @GetMapping("/accounts")
    public String getAccounts(Model model, @RequestParam("last_month") Optional<Integer> opt_last_month) {
        List<ClientEntity> clientEntityList;
        if (opt_last_month.isPresent() && opt_last_month.get() == 1)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            Date fechaHaceUnMes = cal.getTime();
            Timestamp timestampHaceUnMes = new Timestamp(fechaHaceUnMes.getTime());
            clientEntityList = clientRepository.findAllInactiveFrom(timestampHaceUnMes);

        }
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("companies", companyRepository.findAll());

        return ("employee/accounts");
    }

    @GetMapping("/inactive")
    public String getInactive(Model model) {

        return ("employee/inactive_accounts");
    }


    @GetMapping("/suspicious")
    public String getSuspicious(Model model) {

        return ("employee/suspicious");
    }

    @GetMapping("/register")
    public String getRegister(Model model)
    {
        model.addAttribute("newEmployee", new EmployeeEntity());
        return ("employee/register");
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute("newEmployee") EmployeeEntity employee)
    {
        System.out.println(employee.getId());
        System.out.println(employee.getDni());
        return ("redirect:/employee/");
    }
}
