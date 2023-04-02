package com.taw.polybank.controller;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.entity.ClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
