package com.taw.polybank.dto;

public class BankAccountDTO {
    private int id;
    private String iban;
    private boolean active;
    private double balance;
    private ClientDTO clientByClientId;
    private BadgeDTO badgeByBadgeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    public ClientDTO getClientByClientId() {
        return clientByClientId;
    }

    public void setClientByClientId(ClientDTO clientByClientId) {
        this.clientByClientId = clientByClientId;
    }

    public BadgeDTO getBadgeByBadgeId() {
        return badgeByBadgeId;
    }

    public void setBadgeByBadgeId(BadgeDTO badgeByBadgeId) {
        this.badgeByBadgeId = badgeByBadgeId;
    }

}
