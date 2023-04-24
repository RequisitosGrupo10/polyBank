package com.taw.polybank.controller.company;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.CompanyRepository;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.dto.CompanyDTO;
import com.taw.polybank.entity.CompanyEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
                CompanyEntity company = companyRepository.findCompanyRepresentedByClient(client.getId());
                if (company == null) {
                    company = companyRepository.findCompanyRepresentedByClientUsingAuthAcc(client.getId());
                }
                session.setAttribute("company", company);
                if (client.getAuthorizedAccountsById().size() >= 1 && client.getAuthorizedAccountsById().stream().reduce((a, b) -> a).orElse(null).getBlocked() == (byte) 1) {
                    return "redirect:/company/user/blockedUser";
                }
                return "redirect:/company/user/";
            }
        }
        model.addAttribute("error", "User with given ID and password is not found");
        return "/login";
    }

}
