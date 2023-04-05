package com.taw.polybank.controller.company;

import com.taw.polybank.dao.*;
import com.taw.polybank.entity.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/company")
public class RegisterCompany {

    @Autowired
    protected BadgeRepository badgeRepository;

    @Autowired
    protected BankAccountRepository bankAccountRepository;

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected CompanyRepository companyRepository;

    @Autowired
    protected RequestRepository requestRepository;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @GetMapping("/registerCompany")
    public String doRegister(Model model){
        CompanyEntity company = new CompanyEntity();
        model.addAttribute("company", company);

        List<BadgeEntity> badgeList = badgeRepository.findAll();
        model.addAttribute("badgeList", badgeList);

        return "/company/registerCompany";
    }


    @PostMapping("/registerCompanyOwner")
    public String doRegisterCompanyRepresentative(@ModelAttribute("company") CompanyEntity company,
                                                  Model model,
                                                  HttpSession session){
        updateBankAccount(company);
        ClientEntity client = new ClientEntity();
        model.addAttribute("client", client);
        session.setAttribute("bankAccount", company.getBankAccountByBankAccountId());
        session.setAttribute("company", company);
        return "/company/registerRepresentative";
    }

    @PostMapping("/saveNewCompany")
    public String doSaveNewCompany(@ModelAttribute("client") ClientEntity client,
                                   Model model,
                                   HttpSession session){
        BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        RequestEntity request = new RequestEntity();

        // filling up bank account fields
        bankAccount.setClientByClientId(client);
        bankAccount.setRequestsById(List.of(request));

        // filling up Client fields
        client.setCreationDate(Timestamp.from(Instant.now()));
        client.setBankAccountsById(List.of(bankAccount));
        client.setRequestsById(List.of(request));

        PasswordManager passwordManager = new PasswordManager();
        passwordManager.savePassword(client);

        // creating activation request
        defineActivationRequest(client, bankAccount, request);

        // saving Entities
        clientRepository.save(client);
        bankAccountRepository.save(bankAccount);
        companyRepository.save(company);
        requestRepository.save(request);

        session.invalidate();

        return "redirect:/";
    }

    private void updateBankAccount(CompanyEntity company) {
        company.getBankAccountByBankAccountId().setCompaniesById(List.of(company));
        BankAccountEntity bankAccount = company.getBankAccountByBankAccountId();
        bankAccount.setActive((byte) 0);
        Random random = new Random();
        StringBuilder iban = new StringBuilder();
        iban.append("ES44 5268 3000 ");
        for(int i = 0; i < 12; i++){
            iban.append(random.nextInt(10));
        }
        bankAccount.setBalance(0.0);
        bankAccount.setIban(iban.toString());
    }

    private void defineActivationRequest(ClientEntity client, BankAccountEntity bankAccount, RequestEntity request) {
        request.setSolved((byte) 0);
        request.setTimestamp(Timestamp.from(Instant.now()));
        request.setType("activation");
        request.setDescription("Activate company bank Account");
        request.setApproved((byte) 0);
        request.setBankAccountByBankAccountId(bankAccount);
        List<EmployeeEntity> allManagers = employeeRepository.findAllManagers();
        request.setEmployeeByEmployeeId(allManagers.get(new Random().nextInt(allManagers.size())));
        request.setClientByClientId(client);
    }
}

