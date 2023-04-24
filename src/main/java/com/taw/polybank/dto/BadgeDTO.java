package com.taw.polybank.dto;

import jakarta.persistence.*;

import java.util.Collection;

public class BadgeDTO {
    private int id;
    private double value;
    private String name;

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
