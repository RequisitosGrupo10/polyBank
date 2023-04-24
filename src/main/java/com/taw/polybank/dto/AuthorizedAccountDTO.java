package com.taw.polybank.dto;

public class AuthorizedAccountDTO {
    private boolean blocked;
    private int authorizedAccountId;
    private ClientDTO clientByClientId;
    private BankAccountDTO bankAccountByBankAccountId;

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int getAuthorizedAccountId() {
        return authorizedAccountId;
    }

    public void setAuthorizedAccountId(int authorizedAccountId) {
        this.authorizedAccountId = authorizedAccountId;
    }

    public ClientDTO getClientByClientId() {
        return clientByClientId;
    }

    public void setClientByClientId(ClientDTO clientByClientId) {
        this.clientByClientId = clientByClientId;
    }

    public BankAccountDTO getBankAccountByBankAccountId() {
        return bankAccountByBankAccountId;
    }

    public void setBankAccountByBankAccountId(BankAccountDTO bankAccountByBankAccountId) {
        this.bankAccountByBankAccountId = bankAccountByBankAccountId;
    }
}
