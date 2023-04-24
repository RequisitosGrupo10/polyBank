package com.taw.polybank.entity;

import com.taw.polybank.dto.RequestDTO;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "Request", schema = "polyBank", catalog = "")
public class RequestEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "solved", nullable = false)
    private byte solved;
    @Basic
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;
    @Basic
    @Column(name = "type", nullable = false)
    private Object type;
    @Basic
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    @Basic
    @Column(name = "approved", nullable = true)
    private Byte approved;
    @ManyToOne
    @JoinColumn(name = "Client_id", referencedColumnName = "id", nullable = false)
    private ClientEntity clientByClientId;
    @ManyToOne
    @JoinColumn(name = "BankAccount_id", referencedColumnName = "id", nullable = false)
    private BankAccountEntity bankAccountByBankAccountId;
    @ManyToOne
    @JoinColumn(name = "Employee_id", referencedColumnName = "id", nullable = false)
    private EmployeeEntity employeeByEmployeeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getSolved() {
        return solved;
    }

    public void setSolved(byte solved) {
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

    public Byte getApproved() {
        return approved;
    }

    public void setApproved(Byte approved) {
        this.approved = approved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestEntity request = (RequestEntity) o;

        if (id != request.id) return false;
        if (solved != request.solved) return false;
        if (timestamp != null ? !timestamp.equals(request.timestamp) : request.timestamp != null) return false;
        if (type != null ? !type.equals(request.type) : request.type != null) return false;
        if (description != null ? !description.equals(request.description) : request.description != null) return false;
        if (approved != null ? !approved.equals(request.approved) : request.approved != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) solved;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (approved != null ? approved.hashCode() : 0);
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

    public EmployeeEntity getEmployeeByEmployeeId() {
        return employeeByEmployeeId;
    }

    public void setEmployeeByEmployeeId(EmployeeEntity employeeByEmployeeId) {
        this.employeeByEmployeeId = employeeByEmployeeId;
    }

    public RequestDTO toDTO(){
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(getId());
        requestDTO.setType(getType());
        requestDTO.setClientByClientId(getClientByClientId().toDTO());
        requestDTO.setDescription(getDescription());
        requestDTO.setTimestamp(getTimestamp());
        requestDTO.setApproved(getApproved() != 0);
        requestDTO.setBankAccountByBankAccountId(getBankAccountByBankAccountId().toDTO());
        requestDTO.setSolved(getSolved() != 0);
        return requestDTO;
    }
}
