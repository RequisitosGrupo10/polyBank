package com.taw.polybank.dto;

import java.sql.Timestamp;

public class ClientDTO {
    // It does not provide salt and password due to security concerns

    private int id;
    private String dni;
    private String name;
    private String surname;
    private Timestamp creationDate;

    private boolean isNew;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public boolean getIsNew() { return isNew; }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientDTO clientDTO = (ClientDTO) o;

        if (id != clientDTO.id) return false;
        if (!dni.equals(clientDTO.dni)) return false;
        if (!name.equals(clientDTO.name)) return false;
        return surname.equals(clientDTO.surname);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + dni.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + surname.hashCode();
        return result;
    }


}
