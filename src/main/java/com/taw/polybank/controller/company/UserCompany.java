package com.taw.polybank.controller.company;

import com.taw.polybank.dao.*;
import com.taw.polybank.entity.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/company/user")
public class UserCompany {
    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected AuthorizedAccountRepository authorizedAccountRepository;
    @Autowired
    protected BankAccountRepository bankAccountRepository;
    @Autowired
    protected CompanyRepository companyRepository;
    @Autowired
    protected BadgeRepository badgeRepository;
    @Autowired
    protected BeneficiaryRepository beneficiaryRepository;
    @Autowired
    protected CurrencyExchangeRepository currencyExchangeRepository;
    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    protected TransactionRepository transactionRepository;
    @Autowired
    protected EmployeeRepository employeeRepository;
    @Autowired
    protected RequestRepository requestRepository;

    @GetMapping("/")
    public String showUserHomepage() {
        return "/company/userHomepage";
    }

    @GetMapping("/blockedUser")
    public String blockedUserMenu(Model model) {
        model.addAttribute("message", "Your access have has been revoked. Submit your application to unblock.");

        return "/company/blockedUser";
    }

    @PostMapping("/allegation")
    public String allegation(@RequestParam("msg") String message, HttpSession session, Model model) {

        Client client = (Client) session.getAttribute("client");
        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        BankAccountEntity bankAccount = company.getBankAccountByBankAccountId();

        RequestEntity request = new RequestEntity();

        request.setSolved((byte) 0);
        request.setTimestamp(Timestamp.from(Instant.now()));
        request.setType("activation");
        request.setDescription(message);
        request.setApproved((byte) 0);
        request.setBankAccountByBankAccountId(bankAccount);
        List<EmployeeEntity> allManagers = employeeRepository.findAllManagers();
        request.setEmployeeByEmployeeId(allManagers.get(new Random().nextInt(allManagers.size())));
        request.setClientByClientId(client.getClient());

        requestRepository.save(request);

        model.addAttribute("message", "Allegation successfully submitted. Wait patiently for its resolution.");

        return "/company/blockedUser";
    }

    @GetMapping("/logout")
    public String endSession(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/addRepresentative")
    public String addRepresentative(Model model) {
        model.addAttribute("client", new Client(new ClientEntity(), true));
        return "/company/newRepresentative";
    }

    @PostMapping("/setUpPassword")
    public String setUpPassword(@ModelAttribute("client") Client client,
                                Model model) {
        model.addAttribute("client", client);
        return "/company/setUpPassword";
    }

    @PostMapping("/saveNewPassword")
    public String saveNewPassword(@ModelAttribute("client") Client client,
                                  HttpSession session,
                                  Model model) {
        PasswordManager passwordManager = new PasswordManager();
        if (client.getIsNew()) {
            passwordManager.savePassword(client.getClient());
        } else {
            Client oldClient = (Client) session.getAttribute("client");
            passwordManager.resetPassword(oldClient.getClient(), client.getPassword());
        }
        updateUser(client, session, model);
        return "/company/userHomepage";
    }

    @PostMapping("/saveRepresentative")
    public String save(@ModelAttribute("client") Client client,
                       HttpSession session,
                       Model model) {
        updateUser(client, session, model);
        return "/company/userHomepage";
    }

    private void updateUser(Client client, HttpSession session, Model model) {
        model.addAttribute("message", "User " + client.getName() +
                " " + client.getSurname() + " is successfully saved");

        if (client.getIsNew()) {
            CompanyEntity company = (CompanyEntity) session.getAttribute("company");
            BankAccountEntity bankAccount = company.getBankAccountByBankAccountId();
            AuthorizedAccountEntity authorizedAccount = new AuthorizedAccountEntity();
            authorizedAccount.setClientByClientId(client.getClient());
            authorizedAccount.setBankAccountByBankAccountId(bankAccount);
            authorizedAccount.setBlocked((byte) 0);

            List<AuthorizedAccountEntity> authAccountList = authorizedAccountRepository.findAuthorizedAccountEntitiesOfGivenBankAccount(bankAccount.getId());

            authAccountList.add(authorizedAccount);
            bankAccount.setAuthorizedAccountsById(authAccountList);

            client.setCreationDate(Timestamp.from(Instant.now()));
            client.setAuthorizedAccountsById(List.of(authorizedAccount));

            clientRepository.save(client.getClient());
            authorizedAccountRepository.save(authorizedAccount);
            bankAccountRepository.save(bankAccount);
        } else {
            Client oldClient = (Client) session.getAttribute("client");
            oldClient.setName(client.getName());
            oldClient.setSurname(client.getSurname());
            oldClient.setDni(client.getDni());
            clientRepository.save(oldClient.getClient());
        }
    }

    @GetMapping("/editMyData")
    public String editMyData(HttpSession session,
                             Model model) {
        Client client = (Client) session.getAttribute("client");
        model.addAttribute("client", client);
        return "/company/newRepresentative";
    }

    @GetMapping("/editCompanyData")
    public String editCompanyData(HttpSession session, Model model) {
        model.addAttribute("company", (CompanyEntity) session.getAttribute("company"));
        return "/company/editCompanyData";
    }

    @PostMapping("/updateCompanyData")
    public String updateCompanyData(@ModelAttribute("company") CompanyEntity company, HttpSession session, Model model) {
        CompanyEntity oldCompany = (CompanyEntity) session.getAttribute("company");
        oldCompany.setName(company.getName());
        companyRepository.save(oldCompany);
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
        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        List<ClientEntity> clientList;
        if (clientFilter == null) {
            model.addAttribute("clientFilter", new ClientFilter());
            clientList = clientRepository.findAllRepresentativesOfGivenCompany(company.getId());
        } else {
            model.addAttribute("clientFilter", clientFilter);
            Timestamp registeredAfter = new Timestamp(clientFilter.getRegisteredAfter().getTime());
            Timestamp registeredBefore = new Timestamp(clientFilter.getRegisteredBefore().getTime());
            if (clientFilter.getNameOrSurname().isBlank()) {
                clientList = clientRepository.findAllRepresentativesOfACompanyThatWasRegisteredBetweenDates(company.getId(),
                        registeredBefore, registeredAfter);
            } else {
                clientList = clientRepository.findAllRepresentativesOfACompanyThatHasANameOrSurnameAndWasRegisteredBetweenDates(company.getId(),
                        clientFilter.getNameOrSurname(), registeredBefore, registeredAfter);
            }
        }
        model.addAttribute("clientList", clientList);
        return "/company/allRepresentatives";
    }

    @GetMapping("/blockRepresentative")
    public String blockRepresentative(@RequestParam("id") Integer userId) {
        ClientEntity client = clientRepository.findById(userId).orElse(null);
        Collection<AuthorizedAccountEntity> allAuthorizedAccounts = client.getAuthorizedAccountsById(); //TODO more specific blocking algorithm
        for (AuthorizedAccountEntity authorizedAccount : allAuthorizedAccounts) {
            authorizedAccount.setBlocked((byte) 1);
            authorizedAccountRepository.save(authorizedAccount);
        }
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
// TODO Refactor shitcode
        if (amount <= 0) {
            //fail message negative or zero amount
            model.addAttribute("message", "Money transfer was denied, invalid amount (amount = " + amount + ")");
            return "/company/userHomepage";
        }

        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        Client client = (Client) session.getAttribute("client");
        BankAccountEntity bankAccount = company.getBankAccountByBankAccountId();
        if (bankAccount.getBalance() < amount) {
            //fail message not enough money
            model.addAttribute("message", "Money transfer was unsuccessful, not enough money in your bank account");
            return "/company/userHomepage";
        }

        BadgeEntity originBadge = bankAccount.getBadgeByBadgeId();
        BadgeEntity recipientBadge = new BadgeEntity();
        TransactionEntity transaction = defineTransaction(client, bankAccount);

        List<TransactionEntity> allTransactions = transactionRepository.findTransactionEntitiesByClientByClientIdId(client.getId());
        allTransactions.add(transaction);
        client.setTransactionsById(allTransactions);

        allTransactions = transactionRepository.findTransactionEntitiesByBankAccountByBankAccountIdId(bankAccount.getId());
        allTransactions.add(transaction);
        bankAccount.setTransactionsById(allTransactions);


        BankAccountEntity recipientBankAccount = bankAccountRepository.findBankAccountEntityByIban(iban);
        if (recipientBankAccount != null) {// Internal bank money transfer
            CompanyEntity companyRecipient = recipientBankAccount.getCompaniesById().stream().filter(c -> c.getName().equals(beneficiaryName)).findFirst().orElse(null);
            if (companyRecipient == null && companyRecipient.getName().equals(beneficiaryName)) { // Private Client is going to receive money, Authorized person can not figure as beneficiary only proper owner of the account.
                ClientEntity clientRecipient = recipientBankAccount.getClientByClientId();
                if (!clientRecipient.getName().equals(beneficiaryName)) {
                    // fail message name is not matching
                    model.addAttribute("message", "Money transfer was unsuccessful, recipient name is not correct");
                    return "/company/userHomepage";
                }
                // name matching proceed to transfer.
            }// Company is going to receive money
            recipientBadge = recipientBankAccount.getBadgeByBadgeId();

            BenficiaryEntity beneficiary = beneficiaryRepository.findBenficiaryEntityByNameAndIban(beneficiaryName, iban);
            PaymentEntity payment = definePayment(amount, beneficiary, transaction);

            if (beneficiary == null) {
                beneficiary = defineBeneficiary(beneficiaryName, iban, recipientBadge, payment);
            } else {
                Collection<PaymentEntity> allPayments = beneficiary.getPaymentsById();
                allPayments.add(payment);
                beneficiary.setPaymentsById(allPayments);
            }

            payment.setBenficiaryByBenficiaryId(beneficiary);

            if (originBadge.getId() != recipientBadge.getId()) { // Do we need currency exchange?
                CurrencyExchangeEntity currencyExchange = defineCurrencyExchange(originBadge, recipientBadge, amount, transaction, payment);
                payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
                transaction.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
                updateBadges(originBadge, recipientBadge, currencyExchange);
                currencyExchangeRepository.save(currencyExchange);
                recipientBankAccount.setBalance(recipientBankAccount.getBalance() + currencyExchange.getFinalAmount());
            } else {
                recipientBankAccount.setBalance(recipientBankAccount.getBalance() + amount);
            }
            transaction.setPaymentByPaymentId(payment);
            beneficiaryRepository.save(beneficiary);
            paymentRepository.save(payment);
            bankAccountRepository.save(recipientBankAccount);

        } else { // External bank money transfer
            BenficiaryEntity beneficiary = beneficiaryRepository.findBenficiaryEntityByNameAndIban(beneficiaryName, iban);
            PaymentEntity payment = definePayment(amount, beneficiary, transaction);

            if (beneficiary == null) {
                List<BadgeEntity> allBadges = badgeRepository.findAll();
                recipientBadge = allBadges.get(new java.util.Random().nextInt(allBadges.size())); // Assign random badge due to it unknown, and we can simulate this way international transactions
                beneficiary = defineBeneficiary(beneficiaryName, iban, recipientBadge, payment);
            } else {
                recipientBadge = badgeRepository.findBadgeEntityByName(beneficiary.getBadge());
                Collection<PaymentEntity> allPayments = beneficiary.getPaymentsById();
                allPayments.add(payment);
                beneficiary.setPaymentsById(allPayments);
            }
            payment.setBenficiaryByBenficiaryId(beneficiary);

            if (originBadge.getId() != recipientBadge.getId()) { // Do we need currency exchange?
                CurrencyExchangeEntity currencyExchange = defineCurrencyExchange(originBadge, recipientBadge, amount, transaction, payment);
                payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
                transaction.setCurrencyExchangeByCurrencyExchangeId(currencyExchange);
                updateBadges(originBadge, recipientBadge, currencyExchange);
                currencyExchangeRepository.save(currencyExchange);
            }
            transaction.setPaymentByPaymentId(payment);
            beneficiaryRepository.save(beneficiary);
            paymentRepository.save(payment);
        }

        transactionRepository.save(transaction);
        clientRepository.save(client.getClient());
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);

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

    @PostMapping("/makeExchange")
    public String makeExchange(@ModelAttribute BadgeEntity targetBadge, HttpSession session, Model model) {

        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        Client client = (Client) session.getAttribute("client");
        BankAccountEntity bankAccount = company.getBankAccountByBankAccountId();
        BadgeEntity currentBadge = bankAccount.getBadgeByBadgeId();
        targetBadge = badgeRepository.findById(targetBadge.getId()).get();
        TransactionEntity transaction = defineTransaction(client, bankAccount);

        BenficiaryEntity beneficiary = beneficiaryRepository.findBenficiaryEntityByNameAndIban(company.getName(), bankAccount.getIban());
        PaymentEntity payment = definePayment(bankAccount.getBalance(), beneficiary, transaction);

        if (beneficiary == null) {
            beneficiary = defineBeneficiary(company.getName(), bankAccount.getIban(), targetBadge, payment);
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
            updateBadges(currentBadge, targetBadge, currencyExchange);
            currencyExchangeRepository.save(currencyExchange);
            bankAccount.setBalance(currencyExchange.getFinalAmount());
            bankAccount.setBadgeByBadgeId(targetBadge);
            beneficiaryRepository.save(beneficiary);
            paymentRepository.save(payment);
            transactionRepository.save(transaction);
            clientRepository.save(client.getClient());
            bankAccountRepository.save(bankAccount);
            model.addAttribute("message", currencyExchange.getInitialAmount() + " " + currentBadge.getName() + " was successfully exchanged to " + currencyExchange.getFinalAmount() + " " + targetBadge.getName());
        } else {
            model.addAttribute("message", "No exchange was made, chosen currency is actual currency of your bank account.");
        }
        return "/company/userHomepage";
    }

    private PaymentEntity definePayment(Double amount, BenficiaryEntity beneficiary, TransactionEntity transaction) {
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(amount);
        payment.setBenficiaryByBenficiaryId(beneficiary);
        payment.setTransactionsById(List.of(transaction));
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

    private TransactionEntity defineTransaction(Client client, BankAccountEntity bankAccount) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTimestamp(Timestamp.from(Instant.now()));
        transaction.setClientByClientId(client.getClient());
        transaction.setBankAccountByBankAccountId(bankAccount);
        return transaction;
    }

    private void updateBadges(BadgeEntity originBadge, BadgeEntity recipientBadge, CurrencyExchangeEntity currencyExchange) {
        List<CurrencyExchangeEntity> allOriginCurrencyExchange = currencyExchangeRepository.findCurrencyExchangeEntitiesByBadgeByInitialBadgeIdId(originBadge.getId());
        allOriginCurrencyExchange.add(currencyExchange);
        originBadge.setCurrencyExchangesById(allOriginCurrencyExchange);

        List<CurrencyExchangeEntity> allFinalCurrencyExchange = currencyExchangeRepository.findCurrencyExchangeEntitiesByBadgeByFinalBadgeIdId(recipientBadge.getId());
        allFinalCurrencyExchange.add(currencyExchange);
        recipientBadge.setCurrencyExchangesById(allFinalCurrencyExchange);

        badgeRepository.save(originBadge);
        badgeRepository.save(recipientBadge);
    }

    private CurrencyExchangeEntity defineCurrencyExchange(BadgeEntity originBadge, BadgeEntity recipientBadge, Double amount, TransactionEntity transaction, PaymentEntity payment) {
        CurrencyExchangeEntity currencyExchange = defineCurrencyExchange(originBadge, recipientBadge, amount, transaction);
        currencyExchange.setPaymentsById(List.of(payment));
        return currencyExchange;
    }

    private CurrencyExchangeEntity defineCurrencyExchange(BadgeEntity originBadge, BadgeEntity recipientBadge, Double amount, TransactionEntity transaction) {
        CurrencyExchangeEntity currencyExchange = new CurrencyExchangeEntity();
        currencyExchange.setBadgeByInitialBadgeId(originBadge);
        currencyExchange.setBadgeByFinalBadgeId(recipientBadge);
        currencyExchange.setInitialAmount(amount);
        double amountAfterExchange = (recipientBadge.getValue() / originBadge.getValue()) * amount;
        currencyExchange.setFinalAmount(amountAfterExchange);
        currencyExchange.setTransactionsById(List.of(transaction));
        return currencyExchange;
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
}
