package com.taw.polybank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @GetMapping("/")
    public String doBase()
    {
        return ("employeelogin");
    }

    @GetMapping("/login")
    public String getLogin()
    {
        return ("employeelogin");
    }

    @PostMapping("/login")
    public String postLogin()
    {
        return ("/");
    }
}
