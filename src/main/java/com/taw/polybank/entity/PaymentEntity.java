package com.taw.polybank.entity;

import com.taw.polybank.dto.PaymentDTO;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Payment", schema = "polyBank", catalog = "")
public class PaymentEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "amount", nullable = false, precision = 0)
    private double amount;
    @ManyToOne
    @JoinColumn(name = "Benficiary_id", referencedColumnName = "id", nullable = false)
    private BenficiaryEntity benficiaryByBenficiaryId;
    @ManyToOne
    @JoinColumn(name = "CurrencyExchange_id", referencedColumnName = "id")
    private CurrencyExchangeEntity currencyExchangeByCurrencyExchangeId;
    @OneToMany(mappedBy = "paymentByPaymentId")
    private Collection<TransactionEntity> transactionsById;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentEntity that = (PaymentEntity) o;

        if (id != that.id) return false;
        if (Double.compare(that.amount, amount) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public BenficiaryEntity getBenficiaryByBenficiaryId() {
        return benficiaryByBenficiaryId;
    }

    public void setBenficiaryByBenficiaryId(BenficiaryEntity benficiaryByBenficiaryId) {
        this.benficiaryByBenficiaryId = benficiaryByBenficiaryId;
    }

    public CurrencyExchangeEntity getCurrencyExchangeByCurrencyExchangeId() {
        return currencyExchangeByCurrencyExchangeId;
    }

    public void setCurrencyExchangeByCurrencyExchangeId(CurrencyExchangeEntity currencyExchangeByCurrencyExchangeId) {
        this.currencyExchangeByCurrencyExchangeId = currencyExchangeByCurrencyExchangeId;
    }

    public Collection<TransactionEntity> getTransactionsById() {
        return transactionsById;
    }

    public void setTransactionsById(Collection<TransactionEntity> transactionsById) {
        this.transactionsById = transactionsById;
    }

    public PaymentDTO toDTO() {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setId(getId());
        paymentDTO.setAmount(getAmount());
        paymentDTO.setBenficiaryByBenficiaryId(benficiaryByBenficiaryId.toDTO());
        paymentDTO.setCurrencyExchangeByCurrencyExchangeId(getCurrencyExchangeByCurrencyExchangeId().toDTO());
        return paymentDTO;
    }
}
