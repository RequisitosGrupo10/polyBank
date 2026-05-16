package com.taw.polybank.controller.company

import com.taw.polybank.controller.PasswordManager
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.service.AuthorizedAccountService
import com.taw.polybank.service.ClientService
import com.taw.polybank.service.CompanyService
import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author Illya Rozumovskyy
 */
@Controller
@RequestMapping("/company")
class LoginCompany {
  @Autowired
  protected var clientService: ClientService? = null

  @Autowired
  protected var companyService: CompanyService? = null

  @Autowired
  protected var authorizedAccountService: AuthorizedAccountService? = null

  @PostMapping("/login")
  fun doCompanyLogin(
    @RequestParam("dni") dni: String?,
    @RequestParam("password") password: String?,
    model: Model,
    session: HttpSession
  ): String {
    val client = clientService!!.findByDNI(dni)
    if (client != null) {
      val passwordManager = PasswordManager(clientService!!)
      if (passwordManager.verifyPassword(client, password)) {
        client.setIsNew(false)
        session.setAttribute("client", client)
        val companies = companyService!!.findCompanyRepresentedByClient(client.getId())
        if (companies != null && companies.size > 0) {
          if (companies.size == 1) {
            session.setAttribute("company", companies.get(0))
            session.setAttribute("bankAccount", companies.get(0)!!.getBankAccountByBankAccountId())
            if (clientService!!.isBlocked(client, companies.get(0), authorizedAccountService)) {
              return "redirect:/company/user/blockedUser"
            } else {
              return "redirect:/company/user/"
            }
          } else {
            model.addAttribute("companies", companies)
            return "/company/chooseCompany"
          }
        }
      }
    }
    model.addAttribute("error", "User with given ID and password is not found")
    return "/login"
  }

  @GetMapping("/chooseCompany")
  fun chooseCompany(
    @RequestParam("id") companyId: Int,
    session: HttpSession
  ): String {
    val company = companyService!!.findById(companyId).orElse(null)
    session.setAttribute("company", company)
    session.setAttribute("bankAccount", company!!.getBankAccountByBankAccountId())
    val client = session.getAttribute("client") as ClientDTO?
    if (clientService!!.isBlocked(client, company, authorizedAccountService)) {
      return "redirect:/company/user/blockedUser"
    } else {
      return "redirect:/company/user/"
    }
  }
}
