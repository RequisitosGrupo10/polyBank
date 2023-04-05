package com.taw.polybank.controller.company;

import com.taw.polybank.dao.AuthorizedAccountRepository;
import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.entity.AuthorizedAccountEntity;
import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.CompanyEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/company/user")
public class UserCompany {

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected AuthorizedAccountRepository authorizedAccountRepository;

    @Autowired
    protected BankAccountRepository bankAccountRepository;

    @GetMapping("/")
    public String showUserHomepage(){
        return "/company/userHomepage";
    }

    @GetMapping("/logout")
    public String endSession(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/addRepresentative")
    public String addRepresentative(Model model){
        model.addAttribute("client", new ClientEntity());
        return "/company/newRepresentative";
    }

    @PostMapping("/saveRepresenative")
    public String save(@ModelAttribute("client") ClientEntity client,
                       HttpSession session,
                       Model model){
        model.addAttribute("message", "User " + client.getName() +
                " " + client.getSurname() + " is successfully saved");
        ClientEntity recoveredClient = clientRepository.findById(client.getId()).orElse(null);

        boolean notExist = recoveredClient == null;
        if(notExist){
            CompanyEntity company = (CompanyEntity) session.getAttribute("company");
            BankAccountEntity bankAccount = company.getBankAccountByBankAccountId();
            AuthorizedAccountEntity authorizedAccount = new AuthorizedAccountEntity();
            authorizedAccount.setClientByClientId(client);
            authorizedAccount.setBankAccountByBankAccountId(bankAccount);
            Collection<AuthorizedAccountEntity> authAccountCollection = bankAccount.getAuthorizedAccountsById();
            List<AuthorizedAccountEntity> list = new ArrayList<>(authAccountCollection);
            list.add(authorizedAccount);
            bankAccount.setAuthorizedAccountsById(list);
            clientRepository.save(client);
            authorizedAccountRepository.save(authorizedAccount);
            bankAccountRepository.save(bankAccount);
        } else{

            clientRepository.save(client);
        }

        return "/company/userHomepage";
    }
}
