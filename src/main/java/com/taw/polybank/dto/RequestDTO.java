package com.taw.polybank.dto;

import java.sql.Timestamp;

public class RequestDTO {
    private int id;
    private boolean solved;
    private Timestamp timestamp;
    private Object type;
    private String description;
    private boolean approved;
    private ClientDTO clientByClientId;
    private BankAccountDTO bankAccountByBankAccountId;
    private EmployeeDTO employeeByEmployeeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
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

    public EmployeeDTO getEmployeeByEmployeeId() {
        return employeeByEmployeeId;
    }

    public void setEmployeeByEmployeeId(EmployeeDTO employeeByEmployeeId) {
        this.employeeByEmployeeId = employeeByEmployeeId;
    }
}
