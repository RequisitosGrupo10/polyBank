package com.taw.polybank.controller.atm;

import java.sql.Date;
import java.sql.Timestamp;

public class TransactionFilter {
    private Date timestampBegin;
    private Date timestampEnd;
    private String transactionOwner;
    private String beneficiaryIban;
    private double amount;

    public TransactionFilter(Date timestampBegin, Date timestampEnd, String transactionOwner, String beneficiaryIban, double amount){
        this.timestampBegin = timestampBegin;
        this.timestampEnd = timestampEnd;
        this.transactionOwner = transactionOwner;
        this.beneficiaryIban = beneficiaryIban;
        this.amount = amount;
    }


    public Date getTimestampBegin() {
        return timestampBegin;
    }

    public Date getTimestampEnd() {
        return timestampEnd;
    }

    public String getTransactionOwner() {
        return transactionOwner;
    }

    public String getBeneficiaryIban() {
        return beneficiaryIban;
    }

    public double getAmount() {
        return amount;
    }

    public void setTimestampBegin(Date timestampBegin) {
        this.timestampBegin = timestampBegin;
    }

    public void setTimestampEnd(Date timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public void setTransactionOwner(String transactionOwner) {
        this.transactionOwner = transactionOwner;
    }

    public void setBeneficiaryIban(String beneficiaryIban) {
        this.beneficiaryIban = beneficiaryIban;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
