package com.taw.polybank.controller.atm;

import com.taw.polybank.controller.atm.TransactionFilter;
import com.taw.polybank.dao.*;
import com.taw.polybank.entity.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/atm")
public class ATMController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private CurrencyExchangeRepository currencyExchangeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RequestRepository requestRepository;


    @GetMapping("/")
    public String doIndex(Model model, HttpSession session) {
        ClientEntity client = (ClientEntity) session.getAttribute("client");
        if (client == null) {
            return "atm/index";
        } else {
            List<BankAccountEntity> bankAccounts = bankAccountRepository.findByClientByClientId(client);
            model.addAttribute("bankAccounts", bankAccounts);
            return "atm/user_data";
        }
    }

    @PostMapping("/login")
    public String doMostrarDatos(@RequestParam("userName") String user, @RequestParam("password") String password, Model model, HttpSession session) {
        ClientEntity client = this.clientRepository.autenticar(user, password);
        if (client == null) {
            model.addAttribute("error", "Credenciales incorrectas");
        } else {
            session.setAttribute("client", client);
        }
        return "redirect:/atm/";
    }

    @GetMapping("/editarDatos")
    public String gotoEditarDatos(Model model, HttpSession session) {
        ClientEntity client = (ClientEntity) session.getAttribute("client");
        if (client == null) {
            return "atm/index";
        } else {
            model.addAttribute("client", client);
            return "atm/user_edit";
        }
    }

    @PostMapping("/editarDatos")
    public String doEditarDatos(@ModelAttribute("client") ClientEntity client, Model model, HttpSession session) {
        if (session.getAttribute("client") == null)
            return "atm/index";
        clientRepository.save(client);
        session.setAttribute("client", client);
        //BenficiaryEntity beneficiary = beneficiaryRepository.
        model.addAttribute("bankAccounts", bankAccountRepository.findByClientByClientId(client));
        return "atm/user_data";
    }

    @PostMapping("/enumerarAcciones")
    public String doListarAcciones(@RequestParam(name = "bankAccount", required = false) Integer bankAccountId, HttpSession session, Model model) {

        ClientEntity client = (ClientEntity) session.getAttribute("client");
        if (client == null)
            return "atm/index";

        if (bankAccountId != null) {
            BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId).orElse(null);
            session.setAttribute("bankAccount", bankAccount);
            BadgeEntity badge = badgeRepository.findByBankAccountsById(bankAccount);
            session.setAttribute("badge", badge);
        }

        return "atm/bankAccount_actions";
    }

    @GetMapping("/makeTransfer")
    public String doMenuTransfer(HttpSession session, Model model) {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";
        return "atm/bankAccount_transferMenu";
    }

    @PostMapping("/makeTransfer")
    public String doMakeTransfer(@RequestParam("amount") double amount, @RequestParam("receiver") String receiverIBAN, @RequestParam("receiverName") String receiverName, HttpSession session) {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        BankAccountEntity bankAccountReceiver = bankAccountRepository.findByIban(receiverIBAN).orElse(null);
        BadgeEntity badgeReceiver;
        if(bankAccountReceiver != null) {
            badgeReceiver = badgeRepository.findByBankAccountsById(bankAccountReceiver);
        }else{
            badgeReceiver = (BadgeEntity) session.getAttribute("badge");
        }
        makeTransaction(amount, session, receiverIBAN, receiverName, badgeReceiver);

        return "atm/bankAccount_actions";
    }


    @GetMapping("/takeOut")
    public String menuTakeOut(Model model, HttpSession session) {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        List<BadgeEntity> badges = badgeRepository.findAll();
        model.addAttribute("badges", badges);
        return "atm/bankAccount_takeOut";
    }

    @PostMapping("/takeOut")
    public String doTakeOut(@RequestParam("amount") double amount, @RequestParam("badge") int badgeId, HttpSession session, Model model) {
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
        ClientEntity client = (ClientEntity) session.getAttribute("client");
        BadgeEntity badge = badgeRepository.findById(badgeId).orElse(null);

        makeTransaction(amount, session, bankAccount.getIban(), client.getName(), badge);

        return "atm/bankAccount_actions";
    }

    @GetMapping("/checkTransactions")
    public String listTransactions(HttpSession session, Model model){
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
        List<TransactionEntity> transactions = transactionRepository.findByBankAccountByBankAccountId(bankAccount);
        model.addAttribute("transactions", transactions);
        TransactionFilter filter = new TransactionFilter(Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), "", "", 0.0);
        model.addAttribute("filter", filter);
        return "atm/bankAccount_transactions";
    }

    @PostMapping("/checkTransactions")
    public String filterTransactions(HttpSession session, Model model, @ModelAttribute("filter")TransactionFilter filter){
        if (session.getAttribute("client") == null || session.getAttribute("bankAccount") == null)
            return "atm/index";

        BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
        Timestamp begin = Timestamp.valueOf(filter.getTimestampBegin().toLocalDate().atTime(0,0));
        Timestamp end = Timestamp.valueOf(filter.getTimestampEnd().toLocalDate().atTime(23,59));
        List<ClientEntity> clients = clientRepository.findByNameOrSurname(filter.getTransactionOwner());
        BenficiaryEntity beneficiary = beneficiaryRepository.findByIban(filter.getBeneficiaryIban()).orElse(null);
        List<TransactionEntity> transactions;
        if(filter.getTransactionOwner().isBlank() || filter.getBeneficiaryIban().isBlank()){
            if(filter.getTransactionOwner().isBlank()){
                if(filter.getBeneficiaryIban().isBlank()){
                    transactions = transactionRepository.filterByBankAccount_TimestampRange_Amount(bankAccount,begin,end, filter.getAmount());
                }else {
                    transactions = transactionRepository.filterByBankAccount_TimestampRange_beneficiaryIBAN_Amount(bankAccount,begin,end,beneficiary, filter.getAmount());
                }
            }else{
                transactions = transactionRepository.filterByBankAccount_TimestampRange_TransactionOwner_Amount(bankAccount,begin,end,clients, filter.getAmount());
            }
        }else{
            transactions = transactionRepository.filterByBankAccount_TimestampRange_TransactionOwner_beneficiaryIBAN_Amount(bankAccount, begin, end, clients, beneficiary, filter.getAmount());
        }

        model.addAttribute("transactions", transactions);
        return "atm/bankAccount_transactions";
    }


    private void makeTransaction(double amount, HttpSession session, String ibanReceptor, String nameReceptor, BadgeEntity finalBadge) {

        BenficiaryEntity beneficiary = getOrMakeBeneficiaryEntity(ibanReceptor, nameReceptor, finalBadge);

        //Make (if neccessary) a currency exchange
        CurrencyExchangeEntity currencyExchange = null;
        double finalAmount = amount;
        BadgeEntity emisorBadge = (BadgeEntity) session.getAttribute("badge");
        if (emisorBadge.getId() != finalBadge.getId()) {
            //There exists a currency exchange
            currencyExchange = new CurrencyExchangeEntity();
            currencyExchange.setBadgeByFinalBadgeId(finalBadge);
            currencyExchange.setBadgeByInitialBadgeId(emisorBadge);
            currencyExchange.setInitialAmount(amount);
            finalAmount = amount * emisorBadge.getValue() / finalBadge.getValue();
            currencyExchange.setFinalAmount(finalAmount);

            currencyExchangeRepository.save(currencyExchange);
        }

        //Update the bank account(s)
        BankAccountEntity bankAccountEmisor = (BankAccountEntity) session.getAttribute("bankAccount");
        updateBankAccounts(amount, session, ibanReceptor, finalBadge, finalAmount, emisorBadge, bankAccountEmisor);

        //Make the payment
        PaymentEntity payment = makePayment(amount, beneficiary, currencyExchange);

        //Make the transaction
        makeTransaction(session, payment);

    }

    private void makeTransaction(HttpSession session, PaymentEntity payment) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setBankAccountByBankAccountId((BankAccountEntity) session.getAttribute("bankAccount"));
        transaction.setClientByClientId((ClientEntity) session.getAttribute("client"));
        transaction.setPaymentByPaymentId(payment);
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        transactionRepository.save(transaction);
    }

    private PaymentEntity makePayment(double amount, BenficiaryEntity beneficiary, CurrencyExchangeEntity currencyExchange) {
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(amount);
        if (currencyExchange != null) {
            payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
        }
        payment.setBenficiaryByBenficiaryId(beneficiary);
        paymentRepository.save(payment);
        return payment;
    }

    private void updateBankAccounts(double amount, HttpSession session, String ibanReceptor, BadgeEntity finalBadge, double finalAmount, BadgeEntity emisorBadge, BankAccountEntity bankAccountEmisor) {
        if (!ibanReceptor.equals(bankAccountEmisor.getIban())) {
            //Es una transferencia, se actualiza el dinero de ambas partes
            BankAccountEntity bankAccountReceptor = bankAccountRepository.findByIban(ibanReceptor).orElse(null);
            if (bankAccountReceptor != null) {
                bankAccountReceptor.setBalance(bankAccountReceptor.getBalance() + finalAmount);
                bankAccountRepository.save(bankAccountReceptor);
            }
            bankAccountEmisor.setBalance(bankAccountEmisor.getBalance() - amount);
        } else {
            //Está sacando dinero (ya sea de la misma o distinta moneda), por lo que hay que sacar el dinero final de la cuenta bancaria.
            bankAccountEmisor.setBalance(bankAccountEmisor.getBalance() - (amount * finalBadge.getValue() / emisorBadge.getValue()));
        }
        bankAccountRepository.save(bankAccountEmisor);
        session.setAttribute("bankAccount", bankAccountEmisor);
    }

    private BenficiaryEntity getOrMakeBeneficiaryEntity(String ibanReceptor, String nameReceptor, BadgeEntity finalBadge) {
        //Find or make the Beneficiary
        BenficiaryEntity beneficiary = beneficiaryRepository.findByIban(ibanReceptor).orElse(null);
        if (beneficiary == null) {
            beneficiary = new BenficiaryEntity();
            beneficiary.setName(nameReceptor);
            beneficiary.setIban(ibanReceptor);
            beneficiary.setBadge(finalBadge.getName());
            beneficiary.setSwift("");

            beneficiaryRepository.save(beneficiary);
        }
        return beneficiary;
    }

    @GetMapping("/requestUnban")
    public String doRequestUnban(HttpSession session, Model model){
        ClientEntity client = (ClientEntity) session.getAttribute("client");
        BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
        if(client == null || bankAccount == null){
            return "atm/index";
        }

        List<RequestEntity> requestsNotSolved = requestRepository.findByBankAccountByBankAccountIdAndSolved(bankAccount, (byte) 0);

        if(requestsNotSolved.size()== 0){
            return "atm/requestUnban";
        }

        model.addAttribute("requests", requestsNotSolved);

        return "atm/showRequest";
    }

    @GetMapping("/makeUnbanPetition")
    public String doMakeUnbanPetition(HttpSession session, @RequestParam("description") String description){
        ClientEntity client = (ClientEntity) session.getAttribute("client");
        BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
        if(client == null || bankAccount == null){
            return "atm/index";
        }
            List<EmployeeEntity> employees = employeeRepository.findEmployeeWithMinimmumRequests();

            RequestEntity request = new RequestEntity();
            request.setClientByClientId(client);
            request.setBankAccountByBankAccountId(bankAccount);
            request.setEmployeeByEmployeeId(employees.get(0));
            request.setSolved((byte) 0);
            request.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            request.setType("activation");
            request.setDescription(description == null ? "" : description);

            requestRepository.save(request);

        return "redirect:/atm/requestUnban";
    }


}
