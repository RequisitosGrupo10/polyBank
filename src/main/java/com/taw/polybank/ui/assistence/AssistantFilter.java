package com.taw.polybank.ui.assistence;

public class AssistantFilter {
    private String clientDni;
    private String clientName;
    private Boolean recent;

    public AssistantFilter() {
        this.clientDni = "";
        this.clientName = "";
        this.recent = false;
    }

    public String getClientDni() {
        return clientDni;
    }

    public void setClientDni(String clientDni) {
        this.clientDni = clientDni;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Boolean getRecent() {
        return recent;
    }

    public void setRecent(Boolean recent) {
        this.recent = recent;
    }
}
