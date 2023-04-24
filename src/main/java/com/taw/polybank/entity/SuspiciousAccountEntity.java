package com.taw.polybank.entity;

import com.taw.polybank.dto.SuspiciousAccountDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "SuspiciousAccount", schema = "polyBank", catalog = "")
public class SuspiciousAccountEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "IBAN", nullable = false, length = 34)
    private String iban;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuspiciousAccountEntity that = (SuspiciousAccountEntity) o;

        if (id != that.id) return false;
        if (iban != null ? !iban.equals(that.iban) : that.iban != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (iban != null ? iban.hashCode() : 0);
        return result;
    }

    public SuspiciousAccountDTO toDTO(){
        SuspiciousAccountDTO suspiciousAccountDTO = new SuspiciousAccountDTO();
        suspiciousAccountDTO.setIban(getIban());
        suspiciousAccountDTO.setId(getId());
        return suspiciousAccountDTO;
    }
}
