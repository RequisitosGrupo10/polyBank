package com.taw.polybank.entity;

import com.taw.polybank.dto.CurrencyExchangeDTO;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "CurrencyExchange", schema = "polyBank", catalog = "")
public class CurrencyExchangeEntity {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id", nullable = false)
  private int id;

  @Basic
  @Column(name = "initialAmount", nullable = false, precision = 0)
  private double initialAmount;

  @Basic
  @Column(name = "finalAmount", nullable = false, precision = 0)
  private double finalAmount;

  @ManyToOne
  @JoinColumn(name = "initialBadge_id", referencedColumnName = "id", nullable = false)
  private BadgeEntity badgeByInitialBadgeId;

  @ManyToOne
  @JoinColumn(name = "finalBadge_id", referencedColumnName = "id", nullable = false)
  private BadgeEntity badgeByFinalBadgeId;

  @OneToMany(mappedBy = "currencyExchangeByCurrencyExchangeId")
  private List<PaymentEntity> paymentsById;

  @OneToMany(mappedBy = "currencyExchangeByCurrencyExchangeId")
  private List<TransactionEntity> transactionsById;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public double getInitialAmount() {
    return initialAmount;
  }

  public void setInitialAmount(double initialAmount) {
    this.initialAmount = initialAmount;
  }

  public double getFinalAmount() {
    return finalAmount;
  }

  public void setFinalAmount(double finalAmount) {
    this.finalAmount = finalAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CurrencyExchangeEntity that = (CurrencyExchangeEntity) o;

    if (id != that.id) return false;
    if (Double.compare(that.initialAmount, initialAmount) != 0) return false;
    if (Double.compare(that.finalAmount, finalAmount) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = id;
    temp = Double.doubleToLongBits(initialAmount);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(finalAmount);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public BadgeEntity getBadgeByInitialBadgeId() {
    return badgeByInitialBadgeId;
  }

  public void setBadgeByInitialBadgeId(BadgeEntity badgeByInitialBadgeId) {
    this.badgeByInitialBadgeId = badgeByInitialBadgeId;
  }

  public BadgeEntity getBadgeByFinalBadgeId() {
    return badgeByFinalBadgeId;
  }

  public void setBadgeByFinalBadgeId(BadgeEntity badgeByFinalBadgeId) {
    this.badgeByFinalBadgeId = badgeByFinalBadgeId;
  }

  public List<PaymentEntity> getPaymentsById() {
    return paymentsById;
  }

  public void setPaymentsById(List<PaymentEntity> paymentsById) {
    this.paymentsById = paymentsById;
  }

  public List<TransactionEntity> getTransactionsById() {
    return transactionsById;
  }

  public void setTransactionsById(List<TransactionEntity> transactionsById) {
    this.transactionsById = transactionsById;
  }

  public CurrencyExchangeDTO toDTO() {
    CurrencyExchangeDTO currencyExchangeDTO = new CurrencyExchangeDTO();
    currencyExchangeDTO.setId(getId());
    currencyExchangeDTO.setInitialAmount(getInitialAmount());
    currencyExchangeDTO.setFinalAmount(getFinalAmount());
    currencyExchangeDTO.setBadgeByFinalBadgeId(getBadgeByFinalBadgeId().toDTO());
    currencyExchangeDTO.setBadgeByInitialBadgeId(getBadgeByInitialBadgeId().toDTO());
    return currencyExchangeDTO;
  }
}
