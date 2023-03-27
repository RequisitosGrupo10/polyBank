package com.taw.polybank.controller;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.entity.ClientEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/atm")
public class ATMController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/")
    public String doIndex(Model model, HttpSession session) {
        ClientEntity client = (ClientEntity) session.getAttribute("client");
        if (client == null) {
            return "atm/index";
        }else {
            model.addAttribute("client", client);
            return "atm/user_data";
        }
    }

    @PostMapping("/login")
    public String doMostrarDatos(@RequestParam("userName") String user, @RequestParam("password") String password, Model model, HttpSession session) {
        ClientEntity client = this.clientRepository.autenticar(user, password);
        if (client == null) {
            model.addAttribute("error", "Credenciales incorrectas");
        } else {
            session.setAttribute("client", client);
        }
        return "redirect:/atm/";
    }


}
