package com.taw.polybank.controller;


import com.taw.polybank.dao.*;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.*;
import com.taw.polybank.service.*;
import com.taw.polybank.ui.companyFilters.TransactionFilterIllya;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {
    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected BankAccountRepository bankAccountRepository;
    @Autowired
    protected BankAccountService bankAccountService;
    @Autowired
    protected ClientService clientService;
    @Autowired
    protected CompanyService companyService;
    @Autowired
    protected BadgeService badgeService;
    @Autowired
    protected CurrencyExchangeService currencyExchangeService;
    @Autowired
    protected TransactionService transactionService;
    @Autowired
    protected BeneficiaryService beneficiaryService;
    @Autowired
    protected PaymentService paymentService;

    @GetMapping("/view")
    public String viewClient(Model model, HttpSession session) {
        ClientDTO clientDTO = (ClientDTO) session.getAttribute("client");
        List<BankAccountDTO> accounts = bankAccountService.findByClient(clientDTO);
        model.addAttribute("client", clientDTO);
        model.addAttribute("accounts", accounts);
        return "client/viewData";
    }

    @GetMapping("/edit")
    public String editClient(Model model, HttpSession session) {
        ClientDTO clientDTO = (ClientDTO) session.getAttribute("client");
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
        ClientDTO clientDTO = (ClientDTO) session.getAttribute("client");
        BankAccountDTO account = bankAccountService.findById(accountID);

        if (account.getClientByClientId().equals(clientDTO)) {
            session.setAttribute("account", account);
            return "client/bankAccount/viewData";
        } else {
            System.out.println("ERROR: No accesible.");
            return "redirect:/client/view";
        }
    }

    @GetMapping("/account/transaction")
    public String transferMoneyOnBankAccount (Model model, HttpSession session) {
        return "client/bankAccount/makeTransfer";
    }

    @PostMapping("/account/processTransfer")
    public String processTransfer(@RequestParam("beneficiary") String beneficiaryName,
                                  @RequestParam("iban") String iban,
                                  @RequestParam("amount") Double amount,
                                  HttpSession session, Model model) {
        if (amount <= 0) {
            //fail message negative or zero amount
            model.addAttribute("message", "Money transfer was denied, invalid amount (amount = " + amount + ")");
            return "redirect:/account";
        }

        ClientDTO clientDTO = (ClientDTO) session.getAttribute("client");
        BankAccountDTO account = (BankAccountDTO) session.getAttribute("account");
        if (account.getBalance() < amount) {
            //fail message not enough money
            model.addAttribute("message", "Money transfer was unsuccessful, not enough money in your bank account");
            return "redirect:/client/account?id="+account.getId();
        }

        BadgeDTO originBadge = account.getBadgeByBadgeId();
        BadgeDTO recipientBadge = new BadgeDTO();
        TransactionDTO transaction = defineTransaction(clientDTO, account);
        BenficiaryDTO beneficiary = beneficiaryService.findBenficiaryByNameAndIban(beneficiaryName, iban);
        PaymentDTO payment = definePayment(amount, beneficiary);
        BankAccountDTO recipientBankAccount = bankAccountService.findBankAccountEntityByIban(iban);

        if (recipientBankAccount != null) {// Internal bank money transfer
            CompanyDTO companyRecipient = companyService.findCompanyByName(beneficiaryName);
            if (companyRecipient == null) { // Private Client is going to receive money, Authorized person can not figure as beneficiary only proper owner of the account.
                ClientDTO clientRecipient = recipientBankAccount.getClientByClientId();
                if (!clientRecipient.getName().equals(beneficiaryName)) {
                    // fail message name is not matching
                    model.addAttribute("message", "Money transfer was unsuccessful, recipient name is not correct");
                    return "redirect:/client/account?id="+account.getId();
                }
                // name matching proceed to transfer.
            }// Company is going to receive money
            recipientBadge = recipientBankAccount.getBadgeByBadgeId();

            if (beneficiary == null) {
                beneficiary = defineBeneficiary(beneficiaryName, iban, recipientBadge);
            }

            payment.setBenficiaryByBenficiaryId(beneficiary);

            if (originBadge.getId() != recipientBadge.getId()) { // Do we need currency exchange?
                CurrencyExchangeDTO currencyExchange = defineCurrencyExchange(originBadge, recipientBadge, amount, transaction, payment);
                recipientBankAccount.setBalance(recipientBankAccount.getBalance() + currencyExchange.getFinalAmount());
            } else {
                recipientBankAccount.setBalance(recipientBankAccount.getBalance() + amount);
            }
            transaction.setPaymentByPaymentId(payment);
            bankAccountService.save(recipientBankAccount, clientService, badgeService);

        } else { // External bank money transfer
            if (beneficiary == null) {
                recipientBadge = badgeService.getRandomBadge(); // Assign random badge due to it unknown, and we can simulate this way international transactions
                beneficiary = defineBeneficiary(beneficiaryName, iban, recipientBadge);
            } else {
                recipientBadge = badgeService.findBadgeEntityByName(beneficiary.getBadge());
            }
            payment.setBenficiaryByBenficiaryId(beneficiary);

            if (originBadge.getId() != recipientBadge.getId()) { // Do we need currency exchange?
                CurrencyExchangeDTO currencyExchange = defineCurrencyExchange(originBadge, recipientBadge, amount, transaction, payment);
            }
            transaction.setPaymentByPaymentId(payment);

        }
        beneficiaryService.save(beneficiary);
        paymentService.save(payment, beneficiaryService, currencyExchangeService, badgeService);
        transactionService.save(transaction, clientService, bankAccountService, currencyExchangeService, paymentService, badgeService, beneficiaryService);

        account.setBalance(account.getBalance() - amount);
        bankAccountService.save(account, clientService, badgeService);

        // success message
        model.addAttribute("message", amount + " " + account.getBadgeByBadgeId().getName() + " was successfully transferred to " + beneficiaryName);
        return "redirect:/client/account?id="+account.getId();
    }

    @GetMapping("/account/moneyExchange")
    public String moneyExchangeOnBankAccount (Model model, HttpSession session) {
        List<BadgeDTO>  badgeList = badgeService.findAll();
        BankAccountDTO account = (BankAccountDTO) session.getAttribute("account");
        model.addAttribute("badgeList", badgeList);
        model.addAttribute("account", account);
        return "client/bankAccount/moneyExchange";
    }

    @PostMapping("/account/makeExchange")
    public String makeExchange(@RequestParam("badge") Integer badgeID, HttpSession session, Model model) {

        ClientDTO clientDTO = (ClientDTO) session.getAttribute("client");
        BankAccountDTO account = (BankAccountDTO) session.getAttribute("account");
        BadgeDTO currentBadge = account.getBadgeByBadgeId();
        BadgeDTO targetBadge = badgeService.findById(badgeID);

        TransactionDTO transaction = defineTransaction(clientDTO, account);
        BenficiaryDTO beneficiary = beneficiaryService.findBenficiaryByNameAndIban(
                clientDTO.getName(), account.getIban()
        );

        if (beneficiary == null)
            beneficiary = defineBeneficiary(clientDTO.getName(), account.getIban(), targetBadge);
        PaymentDTO payment = definePayment(account.getBalance(), beneficiary);

        payment.setBenficiaryByBenficiaryId(beneficiary);

        if (currentBadge.getId() != targetBadge.getId()) {
            CurrencyExchangeDTO currencyExchange = defineCurrencyExchange(currentBadge, targetBadge, account.getBalance(), transaction, payment);
            transaction.setPaymentByPaymentId(payment);
            account.setBalance(currencyExchange.getFinalAmount());
            account.setBadgeByBadgeId(targetBadge);
            beneficiary.setBadge(targetBadge.getName());
            beneficiary.setSwift("XXX" + targetBadge.getName() + "BNK");
            beneficiaryService.save(beneficiary);
            paymentService.save(payment, beneficiaryService, currencyExchangeService, badgeService);
            transactionService.save(transaction, clientService, bankAccountService, currencyExchangeService, paymentService, badgeService, beneficiaryService);
            bankAccountService.save(account, clientService, badgeService);
        } else {
            System.out.println("ERROR: Can't change currency to the same badge.");
        }

        return "redirect:/client/account?id="+account.getId();
    }

    @GetMapping("/account/operationHistory")
    public String operationHistory(HttpSession session, Model model) {
        return operationHistoryFilters(null, session, model);
    }

    @PostMapping("/account/operationHistory")
    public String operationHistory(@ModelAttribute("transactionFilter") TransactionFilterIllya transactionFilter, HttpSession session, Model model) {
        return operationHistoryFilters(transactionFilter, session, model);
    }

    private String operationHistoryFilters(TransactionFilterIllya transactionFilter, HttpSession session, Model model) {
        List<TransactionDTO> transactionList;
        BankAccountDTO account = (BankAccountDTO) session.getAttribute("account");

        if (transactionFilter == null) {
            transactionList = transactionService.findTransactionsByBankAccountByBankAccountIdId(account.getId());
            transactionFilter = new TransactionFilterIllya();

        } else {
            Timestamp dateAfter = new Timestamp(transactionFilter.getTransactionAfter().getTime());
            Timestamp dateBefore = new Timestamp(transactionFilter.getTransactionBefore().getTime());
            if (transactionFilter.getSenderId().isBlank() && transactionFilter.getRecipientName().isBlank()) {
                transactionList = transactionService.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
                                account.getId(), dateAfter, dateBefore,
                                transactionFilter.getMinAmount(), transactionFilter.getMaxAmount());

            } else if (!transactionFilter.getSenderId().isBlank() && transactionFilter.getRecipientName().isBlank()) {
                transactionList = transactionService.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
                                account.getId(), dateAfter, dateBefore,
                                transactionFilter.getMinAmount(), transactionFilter.getMaxAmount(),
                                transactionFilter.getSenderId());

            } else if (transactionFilter.getSenderId().isBlank() && !transactionFilter.getRecipientName().isBlank()) {
                transactionList = transactionService.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
                                account.getId(), dateAfter, dateBefore,
                                transactionFilter.getMinAmount(), transactionFilter.getMaxAmount(),
                                transactionFilter.getRecipientName());

            } else {
                transactionList = transactionService.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
                                account.getId(), dateAfter, dateBefore,
                                transactionFilter.getMinAmount(), transactionFilter.getMaxAmount(),
                                transactionFilter.getSenderId(), transactionFilter.getRecipientName());
            }
        }
        model.addAttribute("transactionFilter", transactionFilter);
        model.addAttribute("transactionList", transactionList);
        return "client/bankAccount/operationHistory";
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam("dni") String dni, @RequestParam("password") String password,
                            HttpSession session) {
        ClientEntity client = clientRepository.findByDNI(dni);
        if (client != null) {
            //BCrypt.checkpw(password + client.getSalt(), client.getPassword());
            ClientDTO clientDTO = new ClientDTO(client);
            session.setAttribute("client", clientDTO);
            return "redirect:/client/view";
        }
        return ("redirect:/login");
    }

    private PaymentDTO definePayment(Double amount, BenficiaryDTO beneficiary) {
        PaymentDTO payment = new PaymentDTO();
        payment.setAmount(amount);
        payment.setBenficiaryByBenficiaryId(beneficiary);
        return payment;
    }

    private TransactionDTO defineTransaction(ClientDTO client, BankAccountDTO bankAccount) {
        TransactionDTO transaction = new TransactionDTO();
        transaction.setTimestamp(Timestamp.from(Instant.now()));
        transaction.setClientByClientId(client);
        transaction.setBankAccountByBankAccountId(bankAccount);
        return transaction;
    }

    private CurrencyExchangeDTO defineCurrencyExchange(BadgeDTO originBadge, BadgeDTO recipientBadge,
                                                       Double amount, TransactionDTO transaction,
                                                       PaymentDTO payment) {
        CurrencyExchangeDTO currencyExchange = new CurrencyExchangeDTO();
        currencyExchange.setBadgeByInitialBadgeId(originBadge);
        currencyExchange.setBadgeByFinalBadgeId(recipientBadge);
        currencyExchange.setInitialAmount(amount);
        double amountAfterExchange = (recipientBadge.getValue() / originBadge.getValue()) * amount;
        currencyExchange.setFinalAmount(amountAfterExchange);
        payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
        transaction.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
        currencyExchangeService.save(currencyExchange, badgeService);
        return currencyExchange;
    }

    private BenficiaryDTO defineBeneficiary(String beneficiaryName, String iban, BadgeDTO recipientBadge) {
        BenficiaryDTO beneficiary = new BenficiaryDTO();
        beneficiary.setIban(iban);
        beneficiary.setName(beneficiaryName);
        beneficiary.setBadge(recipientBadge.getName());
        beneficiary.setSwift("XXX" + recipientBadge.getName() + "BNK");
        return beneficiary;
    }
}
