package com.taw.polybank.controller.company;

import com.taw.polybank.dao.AuthorizedAccountRepository;
import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.CompanyRepository;
import com.taw.polybank.entity.AuthorizedAccountEntity;
import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.CompanyEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
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

    @Autowired
    protected CompanyRepository companyRepository;

    @GetMapping("/")
    public String showUserHomepage() {
        return "/company/userHomepage";
    }

    @GetMapping("/blockedUser")
    public String blockedUserMenu(){

        return "/company/blockedUser"; // TODO US-15
    }

    @GetMapping("/logout")
    public String endSession(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/addRepresentative")
    public String addRepresentative(Model model) {
        model.addAttribute("client", new Client(new ClientEntity(), true));
        return "/company/newRepresentative";
    }

    @PostMapping("/setUpPassword")
    public String setUpPassword(@ModelAttribute("client") Client client,
                                Model model) {
        model.addAttribute("client", client);
        return "/company/setUpPassword";
    }

    @PostMapping("/saveNewPassword")
    public String saveNewPassword(@ModelAttribute("client") Client client,
                                  HttpSession session,
                                  Model model) {
        PasswordManager passwordManager = new PasswordManager();
        if (client.getIsNew()) {
            passwordManager.savePassword(client.getClient());
        } else {
            Client oldClient = (Client) session.getAttribute("client");
            passwordManager.resetPassword(oldClient.getClient(), client.getPassword());
        }
        updateUser(client, session, model);
        return "/company/userHomepage";
    }

    @PostMapping("/saveRepresentative")
    public String save(@ModelAttribute("client") Client client,
                       HttpSession session,
                       Model model) {
        updateUser(client, session, model);
        return "/company/userHomepage";
    }

    private void updateUser(Client client, HttpSession session, Model model) {
        model.addAttribute("message", "User " + client.getName() +
                " " + client.getSurname() + " is successfully saved");

        if (client.getIsNew()) {
            CompanyEntity company = (CompanyEntity) session.getAttribute("company");
            BankAccountEntity bankAccount = company.getBankAccountByBankAccountId();
            AuthorizedAccountEntity authorizedAccount = new AuthorizedAccountEntity();
            authorizedAccount.setClientByClientId(client.getClient());
            authorizedAccount.setBankAccountByBankAccountId(bankAccount);
            authorizedAccount.setBlocked((byte) 0);

            List<AuthorizedAccountEntity> authAccountList = authorizedAccountRepository.findAuthorizedAccountEntitiesOfGivenBankAccount(bankAccount.getId());

            authAccountList.add(authorizedAccount);
            bankAccount.setAuthorizedAccountsById(authAccountList);

            client.setCreationDate(Timestamp.from(Instant.now()));
            client.setAuthorizedAccountsById(List.of(authorizedAccount));

            clientRepository.save(client.getClient());
            authorizedAccountRepository.save(authorizedAccount);
            bankAccountRepository.save(bankAccount);
        } else {
            Client oldClient = (Client) session.getAttribute("client");
            oldClient.setName(client.getName());
            oldClient.setSurname(client.getSurname());
            oldClient.setDni(client.getDni());
            clientRepository.save(oldClient.getClient());
        }
    }

    @GetMapping("/editMyData")
    public String editMyData(HttpSession session,
                             Model model) {
        Client client = (Client) session.getAttribute("client");
        model.addAttribute("client", client);
        return "/company/newRepresentative";
    }

    @GetMapping("/editCompanyData")
    public String editCompanyData(HttpSession session, Model model) {
        model.addAttribute("company", (CompanyEntity) session.getAttribute("company"));
        return "/company/editCompanyData";
    }

    @PostMapping("/updateCompanyData")
    public String updateCompanyData(@ModelAttribute("company") CompanyEntity company, HttpSession session, Model model) {
        CompanyEntity oldCompany = (CompanyEntity) session.getAttribute("company");
        oldCompany.setName(company.getName());
        companyRepository.save(oldCompany);
        model.addAttribute("message", "Company name was successfully changed to " + company.getName());
        return "/company/userHomepage";
    }

    @GetMapping("/listAllRepresentatives")
    public String listAllRepresentatives(Model model, HttpSession session) {
        return applyFilters(null, model, session);
    }

    @PostMapping("/listFilteredRepresentatives")
    public String listFilteredRepresentatives(@ModelAttribute("clientFilter") ClientFilter clientFilter, Model model, HttpSession session) {
        return applyFilters(clientFilter, model, session);
    }

    private String applyFilters(ClientFilter clientFilter, Model model, HttpSession session) {
        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        List<ClientEntity> clientList;
        if (clientFilter == null) {
            model.addAttribute("clientFilter", new ClientFilter());
            clientList = clientRepository.findAllRepresentativesOfGivenCompany(company.getId());
        } else {
            model.addAttribute("clientFilter", clientFilter);
            Timestamp registeredAfter = new Timestamp(clientFilter.getRegisteredAfter().getTime());
            Timestamp registeredBefore = new Timestamp(clientFilter.getRegisteredBefore().getTime());
            if(clientFilter.getNameOrSurname().isBlank()){
                clientList = clientRepository.findAllRepresentativesOfACompanyThatWasRegisteredBetweenDates(company.getId(),
                        registeredBefore, registeredAfter);
            }else {
                clientList = clientRepository.findAllRepresentativesOfACompanyThatHasANameOrSurnameAndWasRegisteredBetweenDates(company.getId(),
                        clientFilter.getNameOrSurname(), registeredBefore, registeredAfter);
            }
        }
        model.addAttribute("clientList", clientList);
        return "/company/allRepresentatives";
    }

    @GetMapping("/blockRepresentative")
    public String blockRepresentative(@RequestParam("id") Integer userId){
        ClientEntity client = clientRepository.findById(userId).orElse(null);
        Collection<AuthorizedAccountEntity> allAuthorizedAccounts = client.getAuthorizedAccountsById(); //TODO more specific blocking algorithm
        for(AuthorizedAccountEntity authorizedAccount : allAuthorizedAccounts){
            authorizedAccount.setBlocked((byte) 1);
            authorizedAccountRepository.save(authorizedAccount);
        }
        return "redirect:/company/user/listAllRepresentatives";
    }
}
