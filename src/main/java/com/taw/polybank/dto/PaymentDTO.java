package com.taw.polybank.dto;

import jakarta.persistence.*;

import java.util.Collection;

public class PaymentDTO {
    private int id;
    private double amount;
    private BenficiaryDTO benficiaryByBenficiaryId;
    private CurrencyExchangeDTO currencyExchangeByCurrencyExchangeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public BenficiaryDTO getBenficiaryByBenficiaryId() {
        return benficiaryByBenficiaryId;
    }

    public void setBenficiaryByBenficiaryId(BenficiaryDTO benficiaryByBenficiaryId) {
        this.benficiaryByBenficiaryId = benficiaryByBenficiaryId;
    }

    public CurrencyExchangeDTO getCurrencyExchangeByCurrencyExchangeId() {
        return currencyExchangeByCurrencyExchangeId;
    }

    public void setCurrencyExchangeByCurrencyExchangeId(CurrencyExchangeDTO currencyExchangeByCurrencyExchangeId) {
        this.currencyExchangeByCurrencyExchangeId = currencyExchangeByCurrencyExchangeId;
    }

}
