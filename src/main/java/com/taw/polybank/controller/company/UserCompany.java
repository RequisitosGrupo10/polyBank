package com.taw.polybank.controller.company;

import com.taw.polybank.controller.PasswordManager;
import com.taw.polybank.dao.*;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.*;
import com.taw.polybank.service.*;
import com.taw.polybank.ui.ClientFilter;
import com.taw.polybank.ui.TransactionFilter;
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
    protected AuthorizedAccountRepository authorizedAccountRepository;
    @Autowired
    protected AuthorizedAccountService authorizedAccountService;
    @Autowired
    protected BadgeRepository badgeRepository;
    @Autowired
    protected BadgeService badgeService;
    @Autowired
    protected BankAccountRepository bankAccountRepository;
    @Autowired
    protected BankAccountService bankAccountService;
    @Autowired
    protected BeneficiaryRepository beneficiaryRepository;
    @Autowired
    protected BeneficiaryService beneficiaryService;
    @Autowired
    protected ClientRepository clientRepository; // TODO REMOVE ALL REPOSITORIES
    @Autowired
    protected ClientService clientService;
    @Autowired
    protected CompanyRepository companyRepository;
    @Autowired
    protected CompanyService companyService;
    @Autowired
    protected CurrencyExchangeRepository currencyExchangeRepository;
    @Autowired
    protected CurrencyExchangeService currencyExchangeService;
    @Autowired
    protected EmployeeService employeeService;
    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    protected PaymentService paymentService;
    @Autowired
    protected RequestService requestService;
    @Autowired
    protected TransactionService transactionService;
    @Autowired
    protected TransactionRepository transactionRepository;

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
        //clientService.saveUserSaltAndPassword(client.getId(), saltAndPass[0], saltAndPass[2]);
        client.setIsNew(false);
        authorizedAccountService.save(authorizedAccount, clientService, bankAccountService, badgeService);
        //bankAccountService.save(bankAccount, clientService, badgeService);

        bankAccountService.addAuthorizedAccount(bankAccount, authorizedAccount);
//      clientService.addAuthorizedAccount(client, authorizedAccount); // TODO make sure i really don't need to rewrite it
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
        BadgeEntity badge = new BadgeEntity();
        List<BadgeEntity> badgeList = badgeRepository.findAll();

        model.addAttribute("badge", badge);
        model.addAttribute("badgeList", badgeList);
        return "/company/moneyExchange";
    }

//    @PostMapping("/makeExchange")
//    public String makeExchange(@ModelAttribute BadgeEntity targetBadge, HttpSession session, Model model) {
//
//        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
//        Client client = (Client) session.getAttribute("client");
//        BankAccountEntity bankAccount = company.getBankAccountByBankAccountId();
//        BadgeEntity currentBadge = bankAccount.getBadgeByBadgeId();
//        targetBadge = badgeRepository.findById(targetBadge.getId()).get();
//        TransactionEntity transaction = defineTransaction(client, bankAccount);
//
//        BenficiaryEntity beneficiary = beneficiaryRepository.findBenficiaryEntityByNameAndIban(company.getName(), bankAccount.getIban());
//        PaymentEntity payment = definePayment(bankAccount.getBalance(), beneficiary, transaction);
//
//        if (beneficiary == null) {
//            beneficiary = defineBeneficiary(company.getName(), bankAccount.getIban(), targetBadge, payment);
//        } else {
//            beneficiary.getPaymentsById().add(payment);
//        }
//        payment.setBenficiaryByBenficiaryId(beneficiary);
//
//        if (currentBadge.getId() != targetBadge.getId()) {
//            CurrencyExchangeEntity currencyExchange = defineCurrencyExchange(currentBadge, targetBadge, bankAccount.getBalance(), transaction, payment);
//            transaction.setPaymentByPaymentId(payment);
//            bankAccount.setBalance(currencyExchange.getFinalAmount());
//            bankAccount.setBadgeByBadgeId(targetBadge);
//            beneficiaryRepository.save(beneficiary);
//            paymentRepository.save(payment);
//            transactionRepository.save(transaction);
//            clientRepository.save(client.getClient());
//            bankAccountRepository.save(bankAccount);
//            model.addAttribute("message", currencyExchange.getInitialAmount() + " " + currentBadge.getName() + " was successfully exchanged to " + currencyExchange.getFinalAmount() + " " + targetBadge.getName());
//        } else {
//            model.addAttribute("message", "No exchange was made, chosen currency is actual currency of your bank account.");
//        }
//        return "/company/userHomepage";
//    }

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
    public String operationHistory(@ModelAttribute("transactionFilter") TransactionFilter transactionFilter, HttpSession session, Model model) {
        return operationHistoryFilters(transactionFilter, session, model);
    }


    private String operationHistoryFilters(TransactionFilter transactionFilter, HttpSession session, Model model) {
        List<TransactionEntity> transactionList;
        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        BankAccountEntity bankAccount = company.getBankAccountByBankAccountId();

        if (transactionFilter == null) {
            transactionList = transactionRepository.findTransactionEntitiesByBankAccountByBankAccountIdId(bankAccount.getId());
            transactionFilter = new TransactionFilter();

        } else {
            Timestamp dateAfter = new Timestamp(transactionFilter.getTransactionAfter().getTime());
            Timestamp dateBefore = new Timestamp(transactionFilter.getTransactionBefore().getTime());
            if (transactionFilter.getSenderId().isBlank() && transactionFilter.getRecipientName().isBlank()) {
                transactionList = transactionRepository.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
                                bankAccount.getId(),
                                dateAfter,
                                dateBefore,
                                transactionFilter.getMinAmount(),
                                transactionFilter.getMaxAmount());

            } else if (!transactionFilter.getSenderId().isBlank() && transactionFilter.getRecipientName().isBlank()) {
                transactionList = transactionRepository.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
                                bankAccount.getId(),
                                dateAfter,
                                dateBefore,
                                transactionFilter.getMinAmount(),
                                transactionFilter.getMaxAmount(),
                                transactionFilter.getSenderId());

            } else if (transactionFilter.getSenderId().isBlank() && !transactionFilter.getRecipientName().isBlank()) {
                transactionList = transactionRepository.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
                                bankAccount.getId(),
                                dateAfter,
                                dateBefore,
                                transactionFilter.getMinAmount(),
                                transactionFilter.getMaxAmount(),
                                transactionFilter.getRecipientName());

            } else {
                transactionList = transactionRepository.
                        findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
                                bankAccount.getId(),
                                dateAfter,
                                dateBefore,
                                transactionFilter.getMinAmount(),
                                transactionFilter.getMaxAmount(),
                                transactionFilter.getSenderId(),
                                transactionFilter.getRecipientName());
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
        updateBadges(originBadge, recipientBadge, currencyExchange);
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
