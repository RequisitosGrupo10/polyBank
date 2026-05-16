package com.taw.polybank.dto;

import com.taw.polybank.entity.BadgeEntity;
import jakarta.persistence.*;

/**
 * @author José Manuel Sánchez Rico 50%
 * @author Pablo Ruiz-Cruces 50%
 */
public class BadgeDTO {
  private int id;
  private double value;
  private String name;

  public BadgeDTO() {}

  public BadgeDTO(BadgeEntity badge) {
    this.id = badge.getId();
    this.value = badge.getValue();
    this.name = badge.getName();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
