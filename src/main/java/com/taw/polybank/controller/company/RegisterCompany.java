package com.taw.polybank.controller.company;

import com.taw.polybank.dto.*;
import com.taw.polybank.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/company")
public class RegisterCompany {

    @Autowired
    protected BadgeService badgeService;

    @Autowired
    protected BankAccountService bankAccountService;

    @Autowired
    protected ClientService clientService;

    @Autowired
    protected CompanyService companyService;

    @Autowired
    protected RequestService requestService;

    @Autowired
    protected EmployeeService employeeService;

    @GetMapping("/registerCompany")
    public String doRegister(Model model) {
        CompanyDTO company = new CompanyDTO();
        model.addAttribute("company", company);

        List<BadgeDTO> badgeList = badgeService.findAll();
        model.addAttribute("badgeList", badgeList);

        return "/company/registerCompany";
    }


    @PostMapping("/registerCompanyOwner")
    public String doRegisterCompanyOwner(@ModelAttribute("company") CompanyDTO company,
                                         Model model,
                                         HttpSession session) {
        ClientDTO client = new ClientDTO();
        model.addAttribute("client", client);
        session.setAttribute("bankAccount", company.getBankAccountByBankAccountId());
        session.setAttribute("company", company);
        return "/company/registerOwner";
    }

    @PostMapping("/saveNewCompany")
    public String doSaveNewCompany(@ModelAttribute("client") ClientDTO client,
                                   @RequestParam("password") String password,
                                   Model model,
                                   HttpSession session) {
        BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
        CompanyDTO company = (CompanyDTO) session.getAttribute("company");
        RequestDTO request = new RequestDTO();
        updateBankAccount(bankAccount);
        // filling up bank account fields
        bankAccount.setClientByClientId(client);


        // filling up Client fields
        client.setCreationDate(Timestamp.from(Instant.now()));

        company.setBankAccountByBankAccountId(bankAccount);

        PasswordManager passwordManager = new PasswordManager(clientService);
        passwordManager.savePassword(client, password);

        // creating activation request
        defineActivationRequest(client, bankAccount, request);

        // saving Entities
        clientService.save(client, bankAccount, request);


        companyService.save(company);

        bankAccountService.save(bankAccount, company, request);
        // TODO save company to list of companies
        bankAccount.setCompanyById(company);
        // TODO save request to list of requests
        bankAccount.setRequestsById(request);

        requestRepository.save(request);

        session.invalidate();

        return "redirect:/";
    }

    private void updateBankAccount(BankAccountDTO bankAccount) {
        bankAccount.setActive(false);
        Random random = new Random();
        StringBuilder iban = new StringBuilder();
        iban.append("ES44 5268 3000 ");
        for (int i = 0; i < 12; i++) {
            iban.append(random.nextInt(10));
        }
        bankAccount.setBalance(0.0);
        bankAccount.setIban(iban.toString());
    }

    private void defineActivationRequest(ClientDTO client, BankAccountDTO bankAccount, RequestDTO request) {
        request.setSolved(false);
        request.setTimestamp(Timestamp.from(Instant.now()));
        request.setType("activation");
        request.setDescription("Activate company bank Account");
        request.setApproved(false);
        request.setBankAccountByBankAccountId(bankAccount);

        EmployeeDTO manager = employeeService.findManager();
        request.setEmployeeByEmployeeId(manager);
        request.setClientByClientId(client);
    }
}

