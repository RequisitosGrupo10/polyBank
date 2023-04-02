package com.taw.polybank.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "AuthorizedAccount", schema = "polyBank", catalog = "")
public class AuthorizedAccountEntity {
    @EmbeddedId
    private AuthorizedAccountEntityPK authorizedAccountEntityPK;
    @Basic
    @Column(name = "blocked")
    private byte blocked;
    @ManyToOne
    @JoinColumn(name = "Client_id", referencedColumnName = "id", nullable = false, insertable=false, updatable=false)
    private ClientEntity clientByClientId;
    @ManyToOne
    @JoinColumn(name = "BankAccount_id", referencedColumnName = "id", nullable = false, insertable=false, updatable=false)
    private BankAccountEntity bankAccountByBankAccountId;

    public byte getBlocked() {
        return blocked;
    }

    public void setBlocked(byte blocked) {
        this.blocked = blocked;
    }

    public void setAuthorizedAccountEntityPK(AuthorizedAccountEntityPK authorizedAccountEntityPK) {
        this.authorizedAccountEntityPK = authorizedAccountEntityPK;
    }

    public AuthorizedAccountEntityPK getAuthorizedAccountEntityPK() {
        return authorizedAccountEntityPK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizedAccountEntity that = (AuthorizedAccountEntity) o;
        return Objects.equals(authorizedAccountEntityPK, that.authorizedAccountEntityPK) && blocked == that.blocked;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizedAccountEntityPK, blocked);
    }

    public ClientEntity getClientByClientId() {
        return clientByClientId;
    }


    public BankAccountEntity getBankAccountByBankAccountId() {
        return bankAccountByBankAccountId;
    }
}
