package com.taw.polybank.ui.companyFilters;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

public class TransactionFilter {
    private Date transactionAfter;
    private Date transactionBefore;
    private String senderId;
    private String recipientName;
    private double minAmount;
    private double maxAmount;
    public TransactionFilter() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        transactionAfter = new Date(cal.getTimeInMillis());

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        transactionBefore = new Date(cal.getTimeInMillis());

        this.maxAmount = Double.MAX_VALUE;
    }

    public Date getTransactionAfter() {
        return transactionAfter;
    }

    public void setTransactionAfter(Date transactionAfter) {
        this.transactionAfter = transactionAfter;
    }

    public Date getTransactionBefore() {
        return transactionBefore;
    }

    public void setTransactionBefore(Date transactionBefore) {
        this.transactionBefore = transactionBefore;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }
}
