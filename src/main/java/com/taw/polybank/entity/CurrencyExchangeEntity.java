package com.taw.polybank.entity;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "CurrencyExchange", schema = "polyBank", catalog = "")
public class CurrencyExchangeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "initialAmount")
    private double initialAmount;
    @Basic
    @Column(name = "finalAmount")
    private double finalAmount;
    @ManyToOne
    @JoinColumn(name = "initialBadge_id", referencedColumnName = "id", nullable = false)
    private BadgeEntity badgeByInitialBadgeId;
    @ManyToOne
    @JoinColumn(name = "finalBadge_id", referencedColumnName = "id", nullable = false)
    private BadgeEntity badgeByFinalBadgeId;
    @OneToMany(mappedBy = "currencyExchangeByCurrencyExchangeId")
    private Collection<PaymentEntity> paymentsById;
    @OneToMany(mappedBy = "currencyExchangeByCurrencyExchangeId")
    private Collection<TransactionEntity> transactionsById;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyExchangeEntity that = (CurrencyExchangeEntity) o;
        return id == that.id && Double.compare(that.initialAmount, initialAmount) == 0 && Double.compare(that.finalAmount, finalAmount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, initialAmount, finalAmount);
    }

    public BadgeEntity getBadgeByInitialBadgeId() {
        return badgeByInitialBadgeId;
    }

    public void setBadgeByInitialBadgeId(BadgeEntity badgeByInitialBadgeId) {
        this.badgeByInitialBadgeId = badgeByInitialBadgeId;
    }

    public BadgeEntity getBadgeByFinalBadgeId() {
        return badgeByFinalBadgeId;
    }

    public void setBadgeByFinalBadgeId(BadgeEntity badgeByFinalBadgeId) {
        this.badgeByFinalBadgeId = badgeByFinalBadgeId;
    }

    public Collection<PaymentEntity> getPaymentsById() {
        return paymentsById;
    }

    public void setPaymentsById(Collection<PaymentEntity> paymentsById) {
        this.paymentsById = paymentsById;
    }

    public Collection<TransactionEntity> getTransactionsById() {
        return transactionsById;
    }

    public void setTransactionsById(Collection<TransactionEntity> transactionsById) {
        this.transactionsById = transactionsById;
    }
}
