package com.taw.polybank.controller.company;


import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.entity.ClientEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("company/")
public class LoginCompany {

    @Autowired
    protected ClientRepository clientRepository;

    @PostMapping("/login")
    public String doCompanyLogin(@RequestParam("dni") String dni,
                                 @RequestParam("password") String password,
                                 Model model,
                                 HttpSession session) {
        ClientEntity client = clientRepository.findByDNI(dni);
        if (client != null) {
            PasswordManager passwordManager = new PasswordManager();
            if (passwordManager.verifyPassword(client, password)) {
                session.setAttribute("client", client);
                return "/company/user/";
            }
        }
        String error = "User with given ID and password is not found";
        model.addAttribute("error", error);
        return "redirect:login/";
    }

}
