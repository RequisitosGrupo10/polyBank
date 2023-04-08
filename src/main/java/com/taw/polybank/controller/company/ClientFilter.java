package com.taw.polybank.controller.company;


import java.sql.Date;
import java.time.LocalDate;

public class ClientFilter {
    private String nameOrSurname;
    private Date registeredAfter;
    private Date registeredBefore;

    public ClientFilter() {
        nameOrSurname = "";
        registeredAfter = Date.valueOf(LocalDate.now());
        registeredBefore = Date.valueOf(LocalDate.now());
    }

    public String getNameOrSurname() {
        return nameOrSurname;
    }

    public void setNameOrSurname(String nameOrSurname) {
        this.nameOrSurname = nameOrSurname;
    }

    public Date getRegisteredAfter() {
        return registeredAfter;
    }

    public void setRegisteredAfter(Date registeredAfter) {
        this.registeredAfter = registeredAfter;
    }

    public Date getRegisteredBefore() {
        return registeredBefore;
    }

    public void setRegisteredBefore(Date registeredBefore) {
        this.registeredBefore = registeredBefore;
    }


}
