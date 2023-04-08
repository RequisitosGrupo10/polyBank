package com.taw.polybank.controller.company;

import com.taw.polybank.entity.*;

import java.sql.Timestamp;
import java.util.Collection;

public class Client {
    private ClientEntity client;
    private boolean isNew;


    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public Client(ClientEntity client, boolean isNew) {
        this.client = client;
        this.isNew = isNew;
    }

    public int getId() {
        return client.getId();
    }

    public void setId(int id) {
        client.setId(id);
    }

    public String getDni() {
        return client.getDni();
    }

    public void setDni(String dni) {
        client.setDni(dni);
    }

    public String getName() {
        return client.getName();
    }

    public void setName(String name) {
        client.setName(name);
    }

    public String getPassword() {
        return client.getPassword();
    }

    public void setPassword(String password) {
        client.setPassword(password);
    }

    public String getSalt() {
        return client.getSalt();
    }

    public void setSalt(String salt) {
        client.setSalt(salt);
    }

    public String getSurname() {
        return client.getSurname();
    }

    public void setSurname(String surname) {
        client.setSurname(surname);
    }

    public Timestamp getCreationDate() {
        return client.getCreationDate();
    }

    public void setCreationDate(Timestamp creationDate) {
        client.setCreationDate(creationDate);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client that = (Client) o;

        return that.isNew == this.isNew && this.client.equals(that.client);
    }

    @Override
    public int hashCode() {

        return Boolean.hashCode(isNew) + client.hashCode();
    }

    public Collection<AuthorizedAccountEntity> getAuthorizedAccountsById() {
        return client.getAuthorizedAccountsById();
    }

    public void setAuthorizedAccountsById(Collection<AuthorizedAccountEntity> authorizedAccountsById) {
        client.setAuthorizedAccountsById(authorizedAccountsById);
    }

    public Collection<BankAccountEntity> getBankAccountsById() {
        return client.getBankAccountsById();
    }

    public void setBankAccountsById(Collection<BankAccountEntity> bankAccountsById) {
        client.setBankAccountsById(bankAccountsById);
    }

    public Collection<ChatEntity> getChatsById() {
        return client.getChatsById();
    }

    public void setChatsById(Collection<ChatEntity> chatsById) {
        client.setChatsById(chatsById);
    }

    public Collection<MessageEntity> getMessagesById() {
        return client.getMessagesById();
    }

    public void setMessagesById(Collection<MessageEntity> messagesById) {
        client.setMessagesById(messagesById);
    }

    public Collection<RequestEntity> getRequestsById() {
        return client.getRequestsById();
    }

    public void setRequestsById(Collection<RequestEntity> requestsById) {
        client.setRequestsById(requestsById);
    }

    public Collection<TransactionEntity> getTransactionsById() {
        return client.getTransactionsById();
    }

    public void setTransactionsById(Collection<TransactionEntity> transactionsById) {
        client.setTransactionsById(transactionsById);
    }


}
