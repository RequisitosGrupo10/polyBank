package com.taw.polybank.controller.company;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("company/")
public class LoginCompany {

    @PostMapping("/login")
    public String doLogin(@RequestParam("dni") String dni,
                          @RequestParam("password") String password,
                          Model model){


        return null;
    }

}
