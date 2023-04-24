package com.taw.polybank.entity;

import com.taw.polybank.dto.BadgeDTO;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Badge", schema = "polyBank", catalog = "")
public class BadgeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "value", nullable = false, precision = 0)
    private double value;
    @Basic
    @Column(name = "name", nullable = false, length = 5)
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

        if (id != that.id) return false;
        if (Double.compare(that.value, value) != 0) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
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

    public BadgeDTO toDTO(){
        BadgeDTO badgeDTO = new BadgeDTO();
        badgeDTO.setId(getId());
        badgeDTO.setName(getName());
        badgeDTO.setValue(getValue());
        return badgeDTO;
    }
}
