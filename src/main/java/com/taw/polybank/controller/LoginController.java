package com.taw.polybank.controller;

import com.taw.polybank.dao.*;
import com.taw.polybank.entity.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Random;

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
