package com.taw.polybank.dto;

import jakarta.persistence.*;

import java.util.Collection;

public class CurrencyExchangeDTO {
    private int id;
    private double initialAmount;
    private double finalAmount;
    private BadgeDTO badgeByInitialBadgeId;
    private BadgeDTO badgeByFinalBadgeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }


    public BadgeDTO getBadgeByInitialBadgeId() {
        return badgeByInitialBadgeId;
    }

    public void setBadgeByInitialBadgeId(BadgeDTO badgeByInitialBadgeId) {
        this.badgeByInitialBadgeId = badgeByInitialBadgeId;
    }

    public BadgeDTO getBadgeByFinalBadgeId() {
        return badgeByFinalBadgeId;
    }

    public void setBadgeByFinalBadgeId(BadgeDTO badgeByFinalBadgeId) {
        this.badgeByFinalBadgeId = badgeByFinalBadgeId;
    }

}
