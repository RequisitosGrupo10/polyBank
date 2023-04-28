package com.taw.polybank.controller;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.EmployeeEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;
    @GetMapping("/list")
    public String showClients(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        return "clientList";
    }

    @GetMapping("/edit")
    public String editClient(@RequestParam("id") Integer clientID, Model model) {
        ClientEntity client = this.clientRepository.findById(clientID).orElse(null);
        model.addAttribute("client", client);
        return "clientEdit";
    }

    @GetMapping("/create")
    public String addClient(Model model) {
        ClientEntity client = new ClientEntity();
        model.addAttribute("client", client);
        return "clientAdd";
    }

    @PostMapping("/save")
    public String saveClient (@ModelAttribute("client") ClientEntity client) {
        this.clientRepository.save(client);
        return "redirect:/client/list";
    }

    @GetMapping("/delete")
    public String doBorrar (@RequestParam("id") Integer clientID) {
        this.clientRepository.deleteById(clientID);
        return "redirect:/client/list";
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam("dni") String dni, @RequestParam("password") String password,
                            HttpSession session)
    {
        ClientEntity client = clientRepository.findByDNI(dni);
        if (client != null) {
            // It should be validated : BCrypt.checkpw(password + employee.getSalt(), employee.getPassword())
            session.setAttribute("clientID", client.getId());
            return "redirect:/client/assistance/";
        }
        return ("redirect:/login");
    }
}
