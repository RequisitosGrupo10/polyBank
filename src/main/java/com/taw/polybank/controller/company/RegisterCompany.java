package com.taw.polybank.controller.company;

import com.taw.polybank.controller.PasswordManager;
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

/**
 * @author Illya Rozumovskyy
 */
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
        List<BadgeDTO> badgeList = badgeService.findAll();
        model.addAttribute("badgeList", badgeList);

        return "/company/registerCompany";
    }


    @PostMapping("/registerCompanyOwner")
    public String doRegisterCompanyOwner(@RequestParam("name") String companyName,
                                         @RequestParam("badge") Integer badgeId,
                                         Model model,
                                         HttpSession session) {
        ClientDTO client = new ClientDTO();
        model.addAttribute("client", client);

        CompanyDTO company = new CompanyDTO();
        company.setName(companyName);

        BadgeDTO badge = badgeService.findById(badgeId);
        BankAccountDTO bankAccount = new BankAccountDTO();
        bankAccount.setBadgeByBadgeId(badge);
        company.setBankAccountByBankAccountId(bankAccount);
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

        PasswordManager passwordManager = new PasswordManager(clientService);
        String[] saltAndPass = passwordManager.savePassword(client, password);

        // creating activation request
        defineActivationRequest(client, bankAccount, request);

        // saving DTOs
        clientService.save(client, saltAndPass);
        companyService.save(company, bankAccountService, clientService, badgeService);
        bankAccount.setId(bankAccountService.getBankAccountId(bankAccount));
        requestService.save(request, clientService, bankAccountService, employeeService, badgeService);

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

