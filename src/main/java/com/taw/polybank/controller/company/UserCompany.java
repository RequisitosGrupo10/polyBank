package com.taw.polybank.controller.company;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company/user")
public class UserCompany {

    @GetMapping("/")
    public String showUserHomepage(){
        return "/company/userHomepage";
    }

    @GetMapping("/logout")
    public String doEndSession(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }
}
