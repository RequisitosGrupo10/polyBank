package com.taw.polybank.controller.company;

import org.apache.logging.log4j.CloseableThreadContext;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;

public class ClientFilter {
    private String nameOrSurname;
    private Date registeredBefore;

    public ClientFilter(){
        nameOrSurname = "";
        registeredBefore = null;
    }

    public String getNameOrSurname() {
        return nameOrSurname;
    }

    public void setNameOrSurname(String nameOrSurname) {
        this.nameOrSurname = nameOrSurname;
    }

    public Date getRegisteredBefore() {
        return registeredBefore;
    }

    public void setRegisteredBefore(Date registeredBefore) {
        this.registeredBefore = registeredBefore;
    }


}
