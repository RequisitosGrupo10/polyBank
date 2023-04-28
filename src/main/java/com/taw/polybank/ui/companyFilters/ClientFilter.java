package com.taw.polybank.ui.companyFilters;


import java.sql.Date;
import java.util.Calendar;


public class ClientFilter {
    private String nameOrSurname;
    private Date registeredAfter;
    private Date registeredBefore;

    public ClientFilter() {
        nameOrSurname = "";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        registeredAfter = new Date(cal.getTimeInMillis());

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        registeredBefore = new Date(cal.getTimeInMillis());
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
