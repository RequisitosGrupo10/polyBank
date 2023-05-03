package com.taw.polybank.controller.company;

import com.taw.polybank.controller.PasswordManager;
import com.taw.polybank.dto.*;
import com.taw.polybank.service.*;
import com.taw.polybank.ui.companyFilters.ClientFilter;
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
@RequestMapping("/company/user")
public class UserCompany {
    @Autowired
    protected AuthorizedAccountService authorizedAccountService;
    @Autowired
    protected BadgeService badgeService;
    @Autowired
    protected BankAccountService bankAccountService;
    @Autowired
    protected BeneficiaryService beneficiaryService;
    @Autowired
    protected ClientService clientService;
    @Autowired
    protected CompanyService companyService;
    @Autowired
    protected CurrencyExchangeService currencyExchangeService;
    @Autowired
    protected EmployeeService employeeService;
    @Autowired
    protected PaymentService paymentService;
    @Autowired
    protected RequestService requestService;
    @Autowired
    protected TransactionService transactionService;

    @GetMapping("/")
    public String showUserHomepage() {
        return "/company/userHomepage";
    }

    @GetMapping("/blockedUser")
    public String blockedUserMenu(Model model, HttpSession session) {
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        model.addAttribute("message", "Your access have has been revoked.");

        BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
        List<RequestDTO> requests = requestService.findUnsolvedUnblockRequestByUserId(client.getId(), bankAccount.getId());
        model.addAttribute("requests", requests);
        return "/company/blockedUser";
    }

    @PostMapping("/allegation")
    public String allegation(@RequestParam("msg") String message, HttpSession session, Model model) {

        ClientDTO client = (ClientDTO) session.getAttribute("client");
        CompanyDTO company = (CompanyDTO) session.getAttribute("company");
        BankAccountDTO bankAccount = company.getBankAccountByBankAccountId();

        RequestDTO request = new RequestDTO();

        request.setSolved(false);
        request.setTimestamp(Timestamp.from(Instant.now()));
        request.setType("activation");
        request.setDescription(message);
        request.setApproved(false);
        request.setBankAccountByBankAccountId(bankAccount);
        EmployeeDTO manager = employeeService.findManager();

        request.setEmployeeByEmployeeId(manager);
        request.setClientByClientId(client);

        requestService.save(request, clientService, bankAccountService, employeeService, badgeService);

        model.addAttribute("message", "Allegation successfully submitted. Wait patiently for its resolution.");

        return "redirect:/company/user/blockedUser";
    }

    @GetMapping("/logout")
    public String endSession(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/addRepresentative")
    public String addRepresentative(Model model) {
        ClientDTO client = new ClientDTO();
        client.setIsNew(true);
        model.addAttribute("client", client);
        return "/company/newRepresentative";
    }

    @PostMapping("/setUpPassword")
    public String setUpPassword(@ModelAttribute("client") ClientDTO client,
                                Model model) {
        model.addAttribute("client", client);
        return "/company/setUpPassword";
    }

    @PostMapping("/saveNewPassword")
    public String saveNewPassword(@ModelAttribute("client") ClientDTO client,
                                  @RequestParam("password") String password,
                                  HttpSession session,
                                  Model model) {
        if (client.getIsNew()) {
            updateUser(client, password, session, model);
        } else {
            ClientDTO oldClient = (ClientDTO) session.getAttribute("client");
            client.setCreationDate(oldClient.getCreationDate());
            PasswordManager passwordManager = new PasswordManager(clientService);
            passwordManager.resetPassword(client, password);
            clientService.save(client);
        }

        return "/company/userHomepage";
    }

    @PostMapping("/saveRepresentative")
    public String save(@ModelAttribute("client") ClientDTO client, HttpSession session) {
        ClientDTO oldClient = (ClientDTO) session.getAttribute("client");
        client.setCreationDate(oldClient.getCreationDate());
        session.setAttribute("client", client);
        clientService.save(client);
        return "/company/userHomepage";
    }

    private void updateUser(ClientDTO client, String password, HttpSession session, Model model) {

        model.addAttribute("message", "User " + client.getName() +
                " " + client.getSurname() + " is successfully saved");

        CompanyDTO company = (CompanyDTO) session.getAttribute("company");
        BankAccountDTO bankAccount = company.getBankAccountByBankAccountId();
        AuthorizedAccountDTO authorizedAccount = new AuthorizedAccountDTO();
        authorizedAccount.setClientByClientId(client);
        authorizedAccount.setBankAccountByBankAccountId(bankAccount);
        authorizedAccount.setBlocked(false);

        client.setCreationDate(Timestamp.from(Instant.now()));

        PasswordManager passwordManager = new PasswordManager(clientService);
        String[] saltAndPass = passwordManager.savePassword(client, password);
        clientService.save(client, saltAndPass);
        client.setIsNew(false);
        authorizedAccountService.save(authorizedAccount, clientService, bankAccountService, badgeService);

        bankAccountService.addAuthorizedAccount(bankAccount, authorizedAccount);
    }

    @GetMapping("/editMyData")
    public String editMyData(HttpSession session,
                             Model model) {
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        model.addAttribute("client", client);
        return "/company/newRepresentative";
    }

    @GetMapping("/editCompanyData")
    public String editCompanyData(HttpSession session, Model model) {
        model.addAttribute("company", (CompanyDTO) session.getAttribute("company"));
        return "/company/editCompanyData";
    }

    @PostMapping("/updateCompanyData")
    public String updateCompanyData(@ModelAttribute("company") CompanyDTO company, HttpSession session, Model model) {
        CompanyDTO oldCompany = (CompanyDTO) session.getAttribute("company");
        oldCompany.setName(company.getName());
        companyService.save(oldCompany);
        model.addAttribute("message", "Company name was successfully changed to " + company.getName());
        return "/company/userHomepage";
    }

    @GetMapping("/listAllRepresentatives")
    public String listAllRepresentatives(Model model, HttpSession session) {
        return applyFilters(null, model, session);
    }

    @PostMapping("/listFilteredRepresentatives")
    public String listFilteredRepresentatives(@ModelAttribute("clientFilter") ClientFilter clientFilter, Model model, HttpSession session) {
        return applyFilters(clientFilter, model, session);
    }

    private String applyFilters(ClientFilter clientFilter, Model model, HttpSession session) {
        CompanyDTO company = (CompanyDTO) session.getAttribute("company");
        List<ClientDTO> clientList;
        if (clientFilter == null) {
            model.addAttribute("clientFilter", new ClientFilter());
            clientList = clientService.findAllRepresentativesOfGivenCompany(company.getId());
        } else {
            model.addAttribute("clientFilter", clientFilter);
            Timestamp registeredAfter = new Timestamp(clientFilter.getRegisteredAfter().getTime());
            Timestamp registeredBefore = new Timestamp(clientFilter.getRegisteredBefore().getTime());
            if (clientFilter.getNameOrSurname().isBlank()) {
                clientList = clientService.findAllRepresentativesOfACompanyThatWasRegisteredBetweenDates(company.getId(),
                        registeredBefore, registeredAfter);
            } else {
                clientList = clientService.findAllRepresentativesOfACompanyThatHasANameOrSurnameAndWasRegisteredBetweenDates(company.getId(),
                        clientFilter.getNameOrSurname(), registeredBefore, registeredAfter);
            }
        }
        model.addAttribute("clientList", clientList);
        model.addAttribute("clientService", clientService);
        model.addAttribute("authorizedAccountService", authorizedAccountService);
        return "/company/allRepresentatives";
    }

    @GetMapping("/blockRepresentative")
    public String blockRepresentative(@RequestParam("id") Integer userId,
                                      HttpSession session) {
        CompanyDTO company = (CompanyDTO) session.getAttribute("company");
        authorizedAccountService.findAndBlockAuthAccOfGivenClientAndCompany(userId, company.getId());
        return "redirect:/company/user/listAllRepresentatives";
    }

    @GetMapping("/newTransfer")
    public String newTransfer() {

        return "/company/newTransfer";
    }

    @PostMapping("/processTransfer")
    public String processTransfer(@RequestParam("beneficiary") String beneficiaryName,
                                  @RequestParam("iban") String iban,
                                  @RequestParam("amount") Double amount,
                                  HttpSession session, Model model) {
        if (amount <= 0) {
            //fail message negative or zero amount
            model.addAttribute("message", "Money transfer was denied, invalid amount (amount = " + amount + ")");
            return "/company/userHomepage";
        }

        CompanyDTO company = (CompanyDTO) session.getAttribute("company");
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        BankAccountDTO bankAccount = company.getBankAccountByBankAccountId();
        if (bankAccount.getBalance() < amount) {
            //fail message not enough money
            model.addAttribute("message", "Money transfer was unsuccessful, not enough money in your bank account");
            return "/company/userHomepage";
        }

        BadgeDTO originBadge = bankAccount.getBadgeByBadgeId();
        BadgeDTO recipientBadge = new BadgeDTO();
        TransactionDTO transaction = defineTransaction(client, bankAccount);
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
                    return "/company/userHomepage";
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

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountService.save(bankAccount, clientService, badgeService);

        // success message
        model.addAttribute("message", amount + " " + bankAccount.getBadgeByBadgeId().getName() + " was successfully transferred to " + beneficiaryName);
        return "/company/userHomepage";
    }

    @GetMapping("/moneyExchange")
    public String moneyExchange(Model model) {
        BadgeDTO badge = new BadgeDTO();
        List<BadgeDTO> badgeList = badgeService.findAll();

        model.addAttribute("badge", badge);
        model.addAttribute("badgeList", badgeList);
        return "/company/moneyExchange";
    }

    @PostMapping("/makeExchange")
    public String makeExchange(@ModelAttribute BadgeDTO targetBadge, HttpSession session, Model model) {
        CompanyDTO company = (CompanyDTO) session.getAttribute("company");
        ClientDTO client = (ClientDTO) session.getAttribute("client");
        BankAccountDTO bankAccount = company.getBankAccountByBankAccountId();
        BadgeDTO currentBadge = bankAccount.getBadgeByBadgeId();
        targetBadge = badgeService.findById(targetBadge.getId());
        TransactionDTO transaction = defineTransaction(client, bankAccount);

        BenficiaryDTO beneficiary = defineBeneficiary(company.getName(), bankAccount.getIban(), targetBadge);
        PaymentDTO payment = definePayment(bankAccount.getBalance(), beneficiary);

        payment.setBenficiaryByBenficiaryId(beneficiary);

        if (currentBadge.getId() != targetBadge.getId()) {
            CurrencyExchangeDTO currencyExchange = defineCurrencyExchange(currentBadge, targetBadge, bankAccount.getBalance(), transaction, payment);
            transaction.setPaymentByPaymentId(payment);
            bankAccount.setBalance(currencyExchange.getFinalAmount());
            bankAccount.setBadgeByBadgeId(targetBadge);
            beneficiaryService.save(beneficiary);
            paymentService.save(payment, beneficiaryService, currencyExchangeService, badgeService);
            transactionService.save(transaction, clientService, bankAccountService, currencyExchangeService, paymentService, badgeService, beneficiaryService);
            bankAccountService.save(bankAccount, clientService, badgeService);
            model.addAttribute("message", currencyExchange.getInitialAmount() + " " + currentBadge.getName() + " was successfully exchanged to " + currencyExchange.getFinalAmount() + " " + targetBadge.getName());
        } else {
            model.addAttribute("message", "No exchange was made, chosen currency is actual currency of your bank account.");
        }
        return "/company/userHomepage";
    }

    private PaymentDTO definePayment(Double amount, BenficiaryDTO beneficiary) {
        PaymentDTO payment = new PaymentDTO();
        payment.setAmount(amount);
        payment.setBenficiaryByBenficiaryId(beneficiary);
        return payment;
    }

    @GetMapping("/operationHistory")
    public String operationHistory(HttpSession session, Model model) {
        return operationHistoryFilters(null, session, model);
    }

    @PostMapping("/operationHistory")
    public String operationHistory(@ModelAttribute("transactionFilter") TransactionFilterIllya transactionFilter, HttpSession session, Model model) {
        return operationHistoryFilters(transactionFilter, session, model);
    }

    private String operationHistoryFilters(TransactionFilterIllya transactionFilter, HttpSession session, Model model) {
        List<TransactionDTO> transactionList;
        CompanyDTO company = (CompanyDTO) session.getAttribute("company");
        BankAccountDTO bankAccount = company.getBankAccountByBankAccountId();

        if (transactionFilter == null) {
            transactionList = transactionService.findTransactionsByBankAccountByBankAccountIdId(bankAccount.getId());
            transactionFilter = new TransactionFilterIllya();

        } else {
            Timestamp dateAfter = new Timestamp(transactionFilter.getTransactionAfter().getTime());
            Timestamp dateBefore = new Timestamp(transactionFilter.getTransactionBefore().getTime());
            if (transactionFilter.getSenderId().isBlank() && transactionFilter.getRecipientName().isBlank()) {
                transactionList = transactionService.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
                                bankAccount.getId(), dateAfter, dateBefore,
                                transactionFilter.getMinAmount(), transactionFilter.getMaxAmount());

            } else if (!transactionFilter.getSenderId().isBlank() && transactionFilter.getRecipientName().isBlank()) {
                transactionList = transactionService.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
                                bankAccount.getId(), dateAfter, dateBefore,
                                transactionFilter.getMinAmount(), transactionFilter.getMaxAmount(),
                                transactionFilter.getSenderId());

            } else if (transactionFilter.getSenderId().isBlank() && !transactionFilter.getRecipientName().isBlank()) {
                transactionList = transactionService.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
                                bankAccount.getId(), dateAfter, dateBefore,
                                transactionFilter.getMinAmount(), transactionFilter.getMaxAmount(),
                                transactionFilter.getRecipientName());

            } else {
                transactionList = transactionService.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
                                bankAccount.getId(), dateAfter, dateBefore,
                                transactionFilter.getMinAmount(), transactionFilter.getMaxAmount(),
                                transactionFilter.getSenderId(), transactionFilter.getRecipientName());
            }
        }
        model.addAttribute("transactionFilter", transactionFilter);
        model.addAttribute("transactionList", transactionList);
        return "/company/operationHistory";
    }

    private TransactionDTO defineTransaction(ClientDTO client, BankAccountDTO bankAccount) {
        TransactionDTO transaction = new TransactionDTO();
        transaction.setTimestamp(Timestamp.from(Instant.now()));
        transaction.setClientByClientId(client);
        transaction.setBankAccountByBankAccountId(bankAccount);
        return transaction;
    }

    private void updateBadges(BadgeDTO originBadge, BadgeDTO recipientBadge, CurrencyExchangeDTO currencyExchange) {
        badgeService.addAndSave(originBadge, currencyExchange);
        badgeService.addAndSave(recipientBadge, currencyExchange);
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
       // updateBadges(originBadge, recipientBadge, currencyExchange);
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
