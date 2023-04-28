package com.taw.polybank.ui.transaction;

import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class TransactionFilter {
    private double amount;
    private String sorting;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
    }
}
