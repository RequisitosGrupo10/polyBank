package com.taw.polybank.dto;

/**
 * @author José Manuel Sánchez Rico
 */
public class SuspiciousAccountDTO {
  private int id;
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
}
