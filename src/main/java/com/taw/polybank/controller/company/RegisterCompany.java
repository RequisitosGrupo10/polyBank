package com.taw.polybank.controller.company;

import com.taw.polybank.dao.*;
import com.taw.polybank.entity.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
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
    protected AuthorizedAccountRepository authorizedAccountRepository;

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

    @PostMapping("/registerCompanyRepresentative")
    public String doRegisterCompanyRepresentative(@ModelAttribute("company") CompanyEntity company,
                                                  Model model,
                                                  HttpSession session){
        updateBankAccount(company);

        AuthorizedAccountEntity authorizedAccount = new AuthorizedAccountEntity();

        authorizedAccount.setClientByClientId(new ClientEntity());

        authorizedAccount.setBankAccountByBankAccountId(company.getBankAccountByBankAccountId());

        authorizedAccount.setBlocked((byte) 0); // active
        model.addAttribute("authorizedAccount", authorizedAccount);
        session.setAttribute("bankAccount", company.getBankAccountByBankAccountId());
        session.setAttribute("company", company);
        return "/company/registerRepresentative";
    }

    @PostMapping("/saveNewCompany")
    public String doSaveNewCompany(@ModelAttribute("authorizedAccount") AuthorizedAccountEntity authorizedAccount,
                                   Model model,
                                   HttpSession session){
        BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        ClientEntity client = authorizedAccount.getClientByClientId();
        RequestEntity request = new RequestEntity();

        // filling up bank account fields
        bankAccount.setAuthorizedAccountsById(List.of(authorizedAccount));
        bankAccount.setClientByClientId(authorizedAccount.getClientByClientId());
        bankAccount.setRequestsById(List.of(request));

        // filling up Client fields
        client.setCreationDate(Timestamp.from(Instant.now()));
        client.setAuthorizedAccountsById(List.of(authorizedAccount));
        client.setBankAccountsById(List.of(bankAccount));
        client.setRequestsById(List.of(request));
        generatePassword(client);

        // filling up Authorized Account fields
        authorizedAccount.setBankAccountByBankAccountId(bankAccount);

        // saving Entities
        clientRepository.save(client);
        bankAccountRepository.save(bankAccount);
        companyRepository.save(company);
        authorizedAccountRepository.save(authorizedAccount);

        // TODO create ticket for accountant

        request.setSolved((byte) 0);
        request.setTimestamp(Timestamp.from(Instant.now()));
        request.setType("activation");
        request.setDescription("Activate company bank Account");
        request.setApproved((byte) 0);
        request.setBankAccountByBankAccountId(bankAccount);
        List<EmployeeEntity> allManagers = employeeRepository.findAllManagers();
        request.setEmployeeByEmployeeId(allManagers.get(new Random().nextInt(allManagers.size())));
        request.setClientByClientId(client);
        requestRepository.save(request);
        session.invalidate();

        return "redirect:/";
    }

    private void generatePassword(ClientEntity client) {
        byte[] bytes = new byte[32];
        Random random = new Random();
        random.nextBytes(bytes);
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(bytes);
        String salt = BCrypt.gensalt("$2b", 15, secureRandom);
        client.setSalt(new String(bytes, StandardCharsets.UTF_8));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 15, secureRandom);
        client.setPassword(encoder.encode(client.getPassword()));
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
}

