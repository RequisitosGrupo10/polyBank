package com.taw.polybank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {


    @GetMapping("/")
    public String doShowIndex(){
        return "index";
    }

    @GetMapping("/login")
    public String doLogin()
    {
        return ("login");
    }

}
