package com.taw.polybank.controller

import com.taw.polybank.dao.AuthorizedAccountRepository
import com.taw.polybank.dao.BankAccountRepository
import com.taw.polybank.dao.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * @author José Manuel Sánchez Rico
 */
@Controller
class LoginController {
    @Autowired
    private val authorizedAccountRepository: AuthorizedAccountRepository? = null

    @Autowired
    private val clientRepository: ClientRepository? = null

    @Autowired
    private val bankAccountRepository: BankAccountRepository? = null

    @GetMapping("/")
    fun doShowIndex(): String = "index"

    @GetMapping("/login")
    fun doLogin(): String = ("login")
}
