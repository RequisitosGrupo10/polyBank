package com.taw.polybank.dto;

import jakarta.persistence.*;

public class CompanyDTO {
    private int id;
    private String name;
    private BankAccountDTO bankAccountByBankAccountId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public BankAccountDTO getBankAccountByBankAccountId() {
        return bankAccountByBankAccountId;
    }

    public void setBankAccountByBankAccountId(BankAccountDTO bankAccountByBankAccountId) {
        this.bankAccountByBankAccountId = bankAccountByBankAccountId;
    }
}
