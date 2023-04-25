package com.taw.polybank.controller.company;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.CompanyRepository;
import com.taw.polybank.entity.AuthorizedAccountEntity;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.CompanyEntity;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;


@Controller
@RequestMapping("/company")
public class LoginCompany {

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected CompanyRepository companyRepository;

    @PostMapping("/login")
    public String doCompanyLogin(@RequestParam("dni") String dni,
                                 @RequestParam("password") String password,
                                 Model model,
                                 HttpSession session) {
        ClientEntity clientEntity = clientRepository.findByDNI(dni);
        if (clientEntity != null) {
            PasswordManager passwordManager = new PasswordManager();
            if (passwordManager.verifyPassword(clientEntity, password)) {
                Client client = new Client(clientEntity, false);
                session.setAttribute("client", client);
                List<CompanyEntity> company = companyRepository.findCompanyRepresentedByClient(client.getId());
                if (company != null && company.size() > 0) {
                    if (company.size() == 1) {
                        session.setAttribute("company", company.get(0));
                        session.setAttribute("bankAccount", company.get(0).getBankAccountByBankAccountId());
                        if (isBlocked(client, company.get(0))) {
                            return "redirect:/company/user/blockedUser";
                        } else {
                            return "redirect:/company/user/";
                        }
                    } else {
                        model.addAttribute("companies", company);
                        return "/company/chooseCompany";
                    }
                }
            }
        }
        model.addAttribute("error", "User with given ID and password is not found");
        return "/login";
    }

    @GetMapping("/chooseCompany")
    public String chooseCompany(@RequestParam("id") int companyId,
                                HttpSession session){
        CompanyEntity company = companyRepository.findById(companyId).orElse(null);
        session.setAttribute("company", company);
        session.setAttribute("bankAccount", company.getBankAccountByBankAccountId());
        Client client = (Client) session.getAttribute("client");
        if(isBlocked(client, company)){
            return "redirect:/company/user/blockedUser";
        } else {
            return "redirect:/company/user/";
        }
    }

    private boolean isBlocked(Client client, CompanyEntity companyEntity) {
        boolean result = companyEntity.getBankAccountByBankAccountId().getAuthorizedAccountsById().stream()
                .filter(authAcc -> authAcc.getClientByClientId().equals(client.getClient()))
                .findFirst()
                .map(authAccount -> authAccount.getBlocked() == (byte) 1 ? true : false)
                .orElse(false);
        return result;
    }
}
