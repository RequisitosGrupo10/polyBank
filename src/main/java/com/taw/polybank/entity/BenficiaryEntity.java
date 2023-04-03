package com.taw.polybank.entity;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Benficiary", schema = "polyBank", catalog = "")
public class BenficiaryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Basic
    @Column(name = "badge", nullable = false, length = 3)
    private String badge;
    @Basic
    @Column(name = "IBAN", nullable = false, length = 34)
    private String iban;
    @Basic
    @Column(name = "swift", nullable = false, length = 45)
    private String swift;
    @OneToMany(mappedBy = "benficiaryByBenficiaryId")
    private Collection<PaymentEntity> paymentsById;

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

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BenficiaryEntity that = (BenficiaryEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (badge != null ? !badge.equals(that.badge) : that.badge != null) return false;
        if (iban != null ? !iban.equals(that.iban) : that.iban != null) return false;
        if (swift != null ? !swift.equals(that.swift) : that.swift != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (badge != null ? badge.hashCode() : 0);
        result = 31 * result + (iban != null ? iban.hashCode() : 0);
        result = 31 * result + (swift != null ? swift.hashCode() : 0);
        return result;
    }

    public Collection<PaymentEntity> getPaymentsById() {
        return paymentsById;
    }

    public void setPaymentsById(Collection<PaymentEntity> paymentsById) {
        this.paymentsById = paymentsById;
    }
}
