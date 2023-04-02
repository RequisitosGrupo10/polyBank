package com.taw.polybank.controller;

import com.taw.polybank.dao.BadgeRepository;
import com.taw.polybank.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Random;

@Controller
public class LoginController {

    @Autowired
    protected BadgeRepository badgeRepository;

    @GetMapping("/login")
    public String doLogin()
    {
        return ("login");
    }

    @GetMapping("/registerCompany")
    public String doRegister(Model model){
        CompanyEntity company = new CompanyEntity();
        model.addAttribute("company", company);

        List<BadgeEntity> badgeList = badgeRepository.findAll();
        model.addAttribute("badgeList", badgeList);

        return "/company/registerCompany";
    }

    @PostMapping ("/registerCompanyRepresentative")
    public String doRegisterCompanyRepresentative(@ModelAttribute("company") CompanyEntity company,
                                                  Model model){
        updateBankAccount(company);

        AuthorizedAccountEntity authorizedAccount = new AuthorizedAccountEntity();
        authorizedAccount.setClientByClientId(new ClientEntity());
        authorizedAccount.setBankAccountByBankAccountId(company.getBankAccountByBankAccountId());
        model.addAttribute("authorizedAccount", authorizedAccount);

        return "/company/registerRepresentative";
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
