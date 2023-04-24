package com.taw.polybank.entity;

import com.taw.polybank.dto.AuthorizedAccountDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "AuthorizedAccount", schema = "polyBank", catalog = "")
public class AuthorizedAccountEntity {
    @Basic
    @Column(name = "blocked", nullable = false)
    private byte blocked;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "AuthorizedAccount_id", nullable = false)
    private int authorizedAccountId;
    @ManyToOne
    @JoinColumn(name = "Client_id", referencedColumnName = "id", nullable = false)
    private ClientEntity clientByClientId;
    @ManyToOne
    @JoinColumn(name = "BankAccount_id", referencedColumnName = "id", nullable = false)
    private BankAccountEntity bankAccountByBankAccountId;

    public byte getBlocked() {
        return blocked;
    }

    public void setBlocked(byte blocked) {
        this.blocked = blocked;
    }

    public int getAuthorizedAccountId() {
        return authorizedAccountId;
    }

    public void setAuthorizedAccountId(int authorizedAccountId) {
        this.authorizedAccountId = authorizedAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorizedAccountEntity that = (AuthorizedAccountEntity) o;

        if (blocked != that.blocked) return false;
        if (authorizedAccountId != that.authorizedAccountId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) blocked;
        result = 31 * result + authorizedAccountId;
        return result;
    }

    public ClientEntity getClientByClientId() {
        return clientByClientId;
    }

    public void setClientByClientId(ClientEntity clientByClientId) {
        this.clientByClientId = clientByClientId;
    }

    public BankAccountEntity getBankAccountByBankAccountId() {
        return bankAccountByBankAccountId;
    }

    public void setBankAccountByBankAccountId(BankAccountEntity bankAccountByBankAccountId) {
        this.bankAccountByBankAccountId = bankAccountByBankAccountId;
    }

    public AuthorizedAccountDTO toDto(){
        AuthorizedAccountDTO authorizedAccountDTO = new AuthorizedAccountDTO();
        authorizedAccountDTO.setAuthorizedAccountId(this.authorizedAccountId);
        authorizedAccountDTO.setBlocked(this.blocked != 0);
        authorizedAccountDTO.setClientByClientId(this.getClientByClientId().toDTO());
        authorizedAccountDTO.setBankAccountByBankAccountId(this.getBankAccountByBankAccountId().toDTO());
        return  authorizedAccountDTO;
    }
}
