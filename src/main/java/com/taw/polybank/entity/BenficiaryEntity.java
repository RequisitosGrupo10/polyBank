package com.taw.polybank.entity;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "Benficiary", schema = "polyBank", catalog = "")
public class BenficiaryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "badge")
    private String badge;
    @Basic
    @Column(name = "IBAN")
    private String iban;
    @Basic
    @Column(name = "swift")
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
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(badge, that.badge) && Objects.equals(iban, that.iban) && Objects.equals(swift, that.swift);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, badge, iban, swift);
    }

    public Collection<PaymentEntity> getPaymentsById() {
        return paymentsById;
    }

    public void setPaymentsById(Collection<PaymentEntity> paymentsById) {
        this.paymentsById = paymentsById;
    }
}
