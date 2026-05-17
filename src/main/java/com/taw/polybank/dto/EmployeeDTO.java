package com.taw.polybank.dto;

import com.taw.polybank.entity.EmployeeEntity;

/**
 * @author José Manuel Sánchez Rico
 */
public class EmployeeDTO {
  // It does not provide salt and password due to security concerns
  private int id;
  private String dni;
  private String name;
  private EmployeeEntity.EmployeeType type;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EmployeeEntity.EmployeeType getType() {
    return type;
  }

  public void setType(EmployeeEntity.EmployeeType type) {
    this.type = type;
  }
}
