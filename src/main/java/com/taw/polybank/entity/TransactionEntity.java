package com.taw.polybank.entity;

import com.taw.polybank.dto.TransactionDTO;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "Transaction", schema = "polyBank", catalog = "")
public class TransactionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;
    @ManyToOne
    @JoinColumn(name = "Client_id", referencedColumnName = "id", nullable = false)
    private ClientEntity clientByClientId;
    @ManyToOne
    @JoinColumn(name = "BankAccount_id", referencedColumnName = "id", nullable = false)
    private BankAccountEntity bankAccountByBankAccountId;
    @ManyToOne
    @JoinColumn(name = "CurrencyExchange_id", referencedColumnName = "id")
    private CurrencyExchangeEntity currencyExchangeByCurrencyExchangeId;
    @ManyToOne
    @JoinColumn(name = "Payment_id", referencedColumnName = "id")
    private PaymentEntity paymentByPaymentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionEntity that = (TransactionEntity) o;

        if (id != that.id) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
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

    public CurrencyExchangeEntity getCurrencyExchangeByCurrencyExchangeId() {
        return currencyExchangeByCurrencyExchangeId;
    }

    public void setCurrencyExchangeByCurrencyExchangeId(CurrencyExchangeEntity currencyExchangeByCurrencyExchangeId) {
        this.currencyExchangeByCurrencyExchangeId = currencyExchangeByCurrencyExchangeId;
    }

    public PaymentEntity getPaymentByPaymentId() {
        return paymentByPaymentId;
    }

    public void setPaymentByPaymentId(PaymentEntity paymentByPaymentId) {
        this.paymentByPaymentId = paymentByPaymentId;
    }

    public TransactionDTO toDTO(){
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(getId());
        transactionDTO.setTimestamp(getTimestamp());
        transactionDTO.setBankAccountByBankAccountId(getBankAccountByBankAccountId().toDTO());
        transactionDTO.setCurrencyExchangeByCurrencyExchangeId(currencyExchangeByCurrencyExchangeId.toDTO());
        transactionDTO.setPaymentByPaymentId(getPaymentByPaymentId().toDTO());
        return transactionDTO;
    }
}
