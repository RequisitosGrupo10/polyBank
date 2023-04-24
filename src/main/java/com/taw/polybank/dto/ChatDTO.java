package com.taw.polybank.dto;

public class ChatDTO {
    private int id;
    private boolean closed;
    private ClientDTO client;
    private EmployeeDTO assistant;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public EmployeeDTO getAssistant() {
        return assistant;
    }

    public void setAssistant(EmployeeDTO assistant) {
        this.assistant = assistant;
    }

    public String isClosedToString() {
        return (this.isClosed())? "Yes" : "No";
    }
}
