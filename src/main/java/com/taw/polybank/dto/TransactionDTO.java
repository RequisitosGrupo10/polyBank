package com.taw.polybank.dto;

import java.sql.Timestamp;

public class TransactionDTO {
    private int id;
    private Timestamp timestamp;
    private ClientDTO clientByClientId;
    private BankAccountDTO bankAccountByBankAccountId;
    private CurrencyExchangeDTO currencyExchangeByCurrencyExchangeId;
    private PaymentDTO paymentByPaymentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public ClientDTO getClientByClientId() {
        return clientByClientId;
    }

    public void setClientByClientId(ClientDTO clientByClientId) {
        this.clientByClientId = clientByClientId;
    }

    public BankAccountDTO getBankAccountByBankAccountId() {
        return bankAccountByBankAccountId;
    }

    public void setBankAccountByBankAccountId(BankAccountDTO bankAccountByBankAccountId) {
        this.bankAccountByBankAccountId = bankAccountByBankAccountId;
    }

    public CurrencyExchangeDTO getCurrencyExchangeByCurrencyExchangeId() {
        return currencyExchangeByCurrencyExchangeId;
    }

    public void setCurrencyExchangeByCurrencyExchangeId(CurrencyExchangeDTO currencyExchangeByCurrencyExchangeId) {
        this.currencyExchangeByCurrencyExchangeId = currencyExchangeByCurrencyExchangeId;
    }

    public PaymentDTO getPaymentByPaymentId() {
        return paymentByPaymentId;
    }

    public void setPaymentByPaymentId(PaymentDTO paymentByPaymentId) {
        this.paymentByPaymentId = paymentByPaymentId;
    }
}
