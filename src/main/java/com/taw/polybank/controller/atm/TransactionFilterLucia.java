package com.taw.polybank.controller.atm;

import java.sql.Date;

public class TransactionFilterLucia {
    private Date timestampBegin;
    private Date timestampEnd;
    private String transactionOwner;
    private String beneficiaryIban;
    private String amountString;

    private Double amount;

    public TransactionFilterLucia(Date timestampBegin, Date timestampEnd, String transactionOwner, String beneficiaryIban, String amount){
        this.timestampBegin = timestampBegin;
        this.timestampEnd = timestampEnd;
        this.transactionOwner = transactionOwner;
        this.beneficiaryIban = beneficiaryIban;
        this.amountString = amount;
        this.amount = Double.parseDouble((amount == null || amount.equals("")) ? "0": amount);
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

    public Double getAmount() {
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

    public void setAmountString(String amount) {
        if(amount == null || amount.equals("")){
            this.amountString = "0.0";
            this.amount = 0.0;
        }else {
            this.amountString = amount;
            this.amount = Double.parseDouble(amount);
        }
    }
}
