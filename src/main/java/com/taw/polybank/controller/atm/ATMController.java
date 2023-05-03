package com.taw.polybank.controller.atm;

import com.taw.polybank.dto.*;
import com.taw.polybank.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/atm")
public class ATMController {
    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private SuspiciousAccountService suspiciousAccountService;

    @GetMapping("/")
    public String doIndex(Model model, HttpSession session) {
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        if (client == null) {
            return "atm/index";
        } else {
            List<BankAccountDTO> bankAccounts = bankAccountService.findByClient(client);
            model.addAttribute("bankAccounts", bankAccounts);
            return "atm/user_data";
        }
    }

    @PostMapping("/login")
    public String doMostrarDatos(@RequestParam("userName") String user, @RequestParam("password") String password, Model model, HttpSession session) {
        ClientDTO client = clientService.autenticar(user, password);
        if (client == null) {
            model.addAttribute("error", "User with given ID and password is not found");
            return "atm/index";
        } else {
            session.setAttribute("client", client);
        }
        return "redirect:/atm/";
    }

    @GetMapping("/editarDatos")
    public String gotoEditarDatos(Model model, HttpSession session) {
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        if (client == null) {
            return "atm/index";
        } else {
            model.addAttribute("client", client);
            return "atm/user_edit";
        }
    }

    @PostMapping("/editarDatos")
    public String doEditarDatos(@ModelAttribute("client") ClientDTO client, Model model, HttpSession session) {
        if (session.getAttribute("client") == null)
            return "atm/index";
        clientService.guardarCliente(client, "");
        beneficiaryService.guardarBeneficiarios(client);
        session.setAttribute("client", client);
        model.addAttribute("bankAccounts", bankAccountService.findByClient(client));
        return "atm/user_data";
    }

    @PostMapping("/changePassword")
    public String doChangePassword(@RequestParam("password") String password, @RequestParam("password2") String password2, Model model, HttpSession session) {
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        if (client == null)
            return "atm/index";
        if(!password.equals(password2)){
            model.addAttribute("error", "Contraseñas no coinciden");
            model.addAttribute("client", client);
            return "atm/user_edit";
        }else if (password.length() < 7){
            model.addAttribute("error", "La contraseña es muy corta. Debe tener más de 7 símbolos.");
            model.addAttribute("client", client);
            return "atm/user_edit";
        }
        clientService.guardarCliente(client, password);
        session.setAttribute("client", client);
        model.addAttribute("bankAccounts", bankAccountService.findByClient(client));
        return "atm/user_data";
    }

    @PostMapping("/enumerarAcciones")
    public String doListarAcciones(@RequestParam(name = "bankAccount", required = false) Integer bankAccountId, HttpSession session) {

        ClientDTO client = (ClientDTO) session.getAttribute("client");
        if (client == null)
            return "atm/index";

        if (bankAccountId != null) {
            BankAccountDTO bankAccount = bankAccountService.findById(bankAccountId);
            session.setAttribute("bankAccount", bankAccount);
            BadgeDTO badge = badgeService.findByBankAccountsById(bankAccount);
            session.setAttribute("badge", badge);
        }

        return "atm/bankAccount_actions";
    }

    @GetMapping("/makeTransfer")
    public String doMenuTransfer(HttpSession session) {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";
        return "atm/bankAccount_transferMenu";
    }

    @PostMapping("/makeTransfer")
    public String doMakeTransfer(@RequestParam("amount") double amount, @RequestParam("receiver") String receiverIBAN, @RequestParam("receiverName") String receiverName, Model model, HttpSession session) {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        BankAccountDTO bankAccountReceiver = bankAccountService.findByIban(receiverIBAN);

        if(suspiciousAccountService.isSuspicious(receiverIBAN)){
            model.addAttribute("error", "The destination account is suspicious. You cannot transfer money to it.");
            return "atm/bankAccount_transferMenu";
        }
        if(bankAccountReceiver != null && !bankAccountReceiver.getClientByClientId().getName().equals(receiverName)){
            model.addAttribute("error", "The name of the proprietary of the destination account is not correct.");
            return "atm/bankAccount_transferMenu";
        }

        BadgeDTO emisorBadge = (BadgeDTO) session.getAttribute("badge");
        BadgeDTO badgeReceiver;
        if(bankAccountReceiver != null) {
            badgeReceiver = badgeService.findByBankAccountsById(bankAccountReceiver);
        }else{
            badgeReceiver = (BadgeDTO) session.getAttribute("badge");
        }

        BankAccountDTO bankAccountEmisor = (BankAccountDTO) session.getAttribute("bankAccount");
        ClientDTO client = (ClientDTO) session.getAttribute("client");

        transactionService.makeTransaction(amount, receiverIBAN, receiverName, badgeReceiver, emisorBadge, bankAccountEmisor, client);

        BankAccountDTO bankAccountDTO = transactionService.updateBankAccount(bankAccountEmisor);
        session.setAttribute("bankAccount", bankAccountDTO);

        return "atm/bankAccount_actions";
    }


    @GetMapping("/takeOut")
    public String menuTakeOut(Model model, HttpSession session) {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        List<BadgeDTO> badges = badgeService.findAllBadges();
        model.addAttribute("badges", badges);

        return "atm/bankAccount_takeOut";
    }

    @PostMapping("/takeOut")
    public String doTakeOut(@RequestParam("amount") double amount, @RequestParam("badge") int badgeId, HttpSession session) {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        BadgeDTO emisorBadge = (BadgeDTO) session.getAttribute("badge");
        BadgeDTO badge = badgeService.findById(badgeId);

        transactionService.makeTransaction(amount, bankAccount.getIban(), client.getName(), badge, emisorBadge, bankAccount, client);

        BankAccountDTO bankAccountDTO = transactionService.updateBankAccount(bankAccount);
        session.setAttribute("bankAccount", bankAccountDTO);

        return "atm/bankAccount_actions";
    }

    @GetMapping("/checkTransactions")
    public String listTransactions(HttpSession session, Model model){
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
        List<TransactionDTO> transactions = transactionService.findByBankAccountByBankAccountId(bankAccount);
        TransactionFilterLucia filter = new TransactionFilterLucia(Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), "", "", "0.0");

        model.addAttribute("transactions", transactions);
        model.addAttribute("filter", filter);
        return "atm/bankAccount_transactions";
    }

    @PostMapping("/checkTransactions")
    public String filterTransactions(HttpSession session, Model model, @ModelAttribute("filter") TransactionFilterLucia filter){
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
        List<TransactionDTO> transactions = transactionService.filter(bankAccount, filter);

        model.addAttribute("transactions", transactions);
        return "atm/bankAccount_transactions";
    }

    @GetMapping("/requestUnban")
    public String doRequestUnban(HttpSession session, Model model){
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
        if(client == null || bankAccount == null){
            return "atm/index";
        }

        List<RequestDTO> requestsNotSolved = requestService.findByBankAccountByBankAccountIdAndAndSolved(bankAccount, false);

        if(requestsNotSolved.size()== 0){
            return "atm/requestUnban";
        }

        model.addAttribute("requests", requestsNotSolved);

        return "atm/showRequest";
    }

    @GetMapping("/makeUnbanPetition")
    public String doMakeUnbanPetition(HttpSession session, @RequestParam("description") String description){
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
        if(client == null || bankAccount == null){
            return "atm/index";
        }

        requestService.createNewRequest(client, bankAccount, "activation", description);

        return "redirect:/atm/requestUnban";
    }


}
