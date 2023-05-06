package com.taw.polybank.controller;

import com.taw.polybank.controller.company.Client;
import com.taw.polybank.dao.*;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.entity.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {
    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected BankAccountRepository bankAccountRepository;
    @Autowired
    protected BadgeRepository badgeRepository;
    @Autowired
    protected CurrencyExchangeRepository currencyExchangeRepository;
    @Autowired
    protected TransactionRepository transactionRepository;
    @Autowired
    protected BeneficiaryRepository beneficiaryRepository;
    @Autowired
    protected PaymentRepository paymentRepository;

    @GetMapping("/view")
    public String viewClient(Model model, HttpSession session) {
        Integer clientID = (Integer) session.getAttribute("clientID");
        ClientEntity client = this.clientRepository.findById(clientID).orElse(null);
        model.addAttribute("client", client);
        return "client/viewData";
    }

    @GetMapping("/edit")
    public String editClient(Model model, HttpSession session) {
        Integer clientID = (Integer) session.getAttribute("clientID");
        ClientEntity client = this.clientRepository.findById(clientID).orElse(null);
        ClientDTO clientDTO = new ClientDTO(client);
        model.addAttribute("client", clientDTO);
        return "client/editData";
    }

    @GetMapping("/register")
    public String addClient(Model model) {
        ClientEntity client = new ClientEntity();
        model.addAttribute("client", client);
        return "client/register";
    }

    @PostMapping("/save")
    public String saveClient (@ModelAttribute("client") ClientDTO clientDTO, HttpSession session) {
        ClientEntity client = this.clientRepository.findById(clientDTO.getId()).orElse(null);
        client.setName(clientDTO.getName());
        client.setSurname(clientDTO.getSurname());
        this.clientRepository.save(client);
        return "redirect:/client/view";
    }

    @GetMapping("/account")
    public String viewBankAccount (@RequestParam("id") Integer accountID, Model model, HttpSession session) {
        Integer clientID = (Integer) session.getAttribute("clientID");
        BankAccountEntity account = this.bankAccountRepository.findById(accountID).orElse(null);
        if (account.getClientByClientId().getId() == clientID) {
            model.addAttribute("account", account);
            return "client/bankAccount/viewData";
        } else {
            System.out.println("ERROR: No accesible.");
            return "redirect:/client/view";
        }
    }

    @GetMapping("/account/moneyExchange")
    public String moneyExchangeOnBankAccount (@RequestParam("id") Integer accountID, Model model, HttpSession session) {
        BadgeEntity badge = new BadgeEntity();
        List<BadgeEntity> badgeList = this.badgeRepository.findAll();
        BankAccountEntity account = this.bankAccountRepository.findById(accountID).orElse(null);
        model.addAttribute("badge", badge);
        model.addAttribute("account", account);
        model.addAttribute("badgeList", badgeList);
        return "client/bankAccount/moneyExchange";
    }

    @PostMapping("/account/makeExchange")
    public String makeExchange(@ModelAttribute BadgeEntity targetBadge, @RequestParam("id") Integer accountID, HttpSession session, Model model) {

        Integer clientID = (Integer) session.getAttribute("clientID");
        ClientEntity client = this.clientRepository.findById(clientID).orElse(null);
        BankAccountEntity bankAccount = this.bankAccountRepository.findById(accountID).orElse(null);
        BadgeEntity currentBadge = bankAccount.getBadgeByBadgeId();
        targetBadge = badgeRepository.findById(targetBadge.getId()).get();
        System.out.println("Current: "+currentBadge.getName()+"/ Target: "+targetBadge.getName());
        TransactionEntity transaction = defineTransaction(client, bankAccount);
        BenficiaryEntity beneficiary = this.beneficiaryRepository.findBenficiaryEntityByNameAndIban(client.getName(), bankAccount.getIban());
        PaymentEntity payment = definePayment(bankAccount.getBalance(), beneficiary, transaction);

        if (beneficiary == null) {
            beneficiary = defineBeneficiary(client.getName(), bankAccount.getIban(), targetBadge, payment);
        } else {
            Collection<PaymentEntity> allPayments = beneficiary.getPaymentsById();
            allPayments.add(payment);
            beneficiary.setPaymentsById(allPayments);
        }

        payment.setBenficiaryByBenficiaryId(beneficiary);

        if (currentBadge.getId() != targetBadge.getId()) {
            CurrencyExchangeEntity currencyExchange = defineCurrencyExchange(currentBadge, targetBadge, bankAccount.getBalance(), transaction, payment);
            payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
            transaction.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
            transaction.setPaymentByPaymentId(payment);
            this.currencyExchangeRepository.save(currencyExchange);
            bankAccount.setBalance(currencyExchange.getFinalAmount());
            bankAccount.setBadgeByBadgeId(targetBadge);
            this.beneficiaryRepository.save(beneficiary);
            this.paymentRepository.save(payment);
            this.transactionRepository.save(transaction);
            this.bankAccountRepository.save(bankAccount);
        } else {
            System.out.println("ERROR: Can't change currency to the same badge.");
        }

        return "redirect:/client/account?id="+accountID;
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam("dni") String dni, @RequestParam("password") String password,
                            HttpSession session) {
        ClientEntity client = clientRepository.findByDNI(dni);
        if (client != null) {
            //BCrypt.checkpw(password + client.getSalt(), client.getPassword());
            session.setAttribute("clientID", client.getId());
            return "redirect:/client/view";
        }
        return ("redirect:/login");
    }
    
    private PaymentEntity definePayment(Double amount, BenficiaryEntity beneficiary, TransactionEntity transaction) {
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(amount);
        payment.setBenficiaryByBenficiaryId(beneficiary);
        payment.setTransactionsById(List.of(transaction));
        return payment;
    }

    private TransactionEntity defineTransaction(ClientEntity client, BankAccountEntity bankAccount) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTimestamp(Timestamp.from(Instant.now()));
        transaction.setClientByClientId(client);
        transaction.setBankAccountByBankAccountId(bankAccount);
        return transaction;
    }

    private BenficiaryEntity defineBeneficiary(String beneficiaryName, String iban, BadgeEntity recipientBadge, PaymentEntity payment) {
        BenficiaryEntity beneficiary = new BenficiaryEntity();
        beneficiary = new BenficiaryEntity();
        beneficiary.setIban(iban);
        beneficiary.setName(beneficiaryName);
        beneficiary.setBadge(recipientBadge.getName());
        beneficiary.setSwift("XXX" + recipientBadge.getName() + "BNK");
        beneficiary.setPaymentsById(List.of(payment));
        return beneficiary;
    }

    private CurrencyExchangeEntity defineCurrencyExchange(BadgeEntity originBadge, BadgeEntity recipientBadge, Double amount, TransactionEntity transaction, PaymentEntity payment) {
        CurrencyExchangeEntity currencyExchange = new CurrencyExchangeEntity();
        currencyExchange.setBadgeByInitialBadgeId(originBadge);
        currencyExchange.setBadgeByFinalBadgeId(recipientBadge);
        currencyExchange.setInitialAmount(amount);
        double amountAfterExchange = (recipientBadge.getValue() / originBadge.getValue()) * amount;
        currencyExchange.setFinalAmount(amountAfterExchange);
        currencyExchange.setTransactionsById(List.of(transaction));
        currencyExchange.setPaymentsById(List.of(payment));
        return currencyExchange;
    }
}
