package com.taw.polybank.entity;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "Badge", schema = "polyBank", catalog = "")
public class BadgeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "value")
    private double value;
    @Basic
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "badgeByBadgeId")
    private Collection<BankAccountEntity> bankAccountsById;
    @OneToMany(mappedBy = "badgeByInitialBadgeId")
    private Collection<CurrencyExchangeEntity> currencyExchangesById;
    @OneToMany(mappedBy = "badgeByFinalBadgeId")
    private Collection<CurrencyExchangeEntity> currencyExchangesById_0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BadgeEntity that = (BadgeEntity) o;
        return id == that.id && Double.compare(that.value, value) == 0 && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, name);
    }

    public Collection<BankAccountEntity> getBankAccountsById() {
        return bankAccountsById;
    }

    public void setBankAccountsById(Collection<BankAccountEntity> bankAccountsById) {
        this.bankAccountsById = bankAccountsById;
    }

    public Collection<CurrencyExchangeEntity> getCurrencyExchangesById() {
        return currencyExchangesById;
    }

    public void setCurrencyExchangesById(Collection<CurrencyExchangeEntity> currencyExchangesById) {
        this.currencyExchangesById = currencyExchangesById;
    }

    public Collection<CurrencyExchangeEntity> getCurrencyExchangesById_0() {
        return currencyExchangesById_0;
    }

    public void setCurrencyExchangesById_0(Collection<CurrencyExchangeEntity> currencyExchangesById_0) {
        this.currencyExchangesById_0 = currencyExchangesById_0;
    }
}
