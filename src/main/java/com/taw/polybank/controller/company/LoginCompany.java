package com.taw.polybank.controller.company;

import com.taw.polybank.controller.PasswordManager;
import com.taw.polybank.dto.AuthorizedAccountDTO;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.dto.CompanyDTO;
import com.taw.polybank.service.AuthorizedAccountService;
import com.taw.polybank.service.ClientService;
import com.taw.polybank.service.CompanyService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/company")
public class LoginCompany {

    @Autowired
    protected ClientService clientService;

    @Autowired
    protected CompanyService companyService;

    @Autowired
    protected AuthorizedAccountService authorizedAccountService;

    @PostMapping("/login")
    public String doCompanyLogin(@RequestParam("dni") String dni,
                                 @RequestParam("password") String password,
                                 Model model,
                                 HttpSession session) {
        ClientDTO client = clientService.findByDNI(dni);
        if (client != null) {
            PasswordManager passwordManager = new PasswordManager(clientService);
            if (passwordManager.verifyPassword(client, password)) {
                client.setIsNew(false);
                session.setAttribute("client", client);
                List<CompanyDTO> companies = companyService.findCompanyRepresentedByClient(client.getId());
                if (companies != null && companies.size() > 0) {
                    if (companies.size() == 1) {
                        session.setAttribute("company", companies.get(0));
                        session.setAttribute("bankAccount", companies.get(0).getBankAccountByBankAccountId());
                        if (clientService.isBlocked(client, companies.get(0), authorizedAccountService)) {
                            return "redirect:/company/user/blockedUser";
                        } else {
                            return "redirect:/company/user/";
                        }
                    } else {
                        model.addAttribute("companies", companies);
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
                                HttpSession session) {
        CompanyDTO company = companyService.findById(companyId).orElse(null);
        session.setAttribute("company", company);
        session.setAttribute("bankAccount", company.getBankAccountByBankAccountId());
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        if (clientService.isBlocked(client, company, authorizedAccountService)) {
            return "redirect:/company/user/blockedUser";
        } else {
            return "redirect:/company/user/";
        }
    }
}
