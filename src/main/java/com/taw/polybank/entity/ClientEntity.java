package com.taw.polybank.entity;

import com.taw.polybank.dto.ClientDTO;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "Client", schema = "polyBank", catalog = "")
public class ClientEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "DNI", nullable = false, length = 45)
    private String dni;
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Basic
    @Column(name = "password", nullable = false, length = 64)
    private String password;
    @Basic
    @Column(name = "salt", nullable = false, length = 32)
    private String salt;
    @Basic
    @Column(name = "surname", nullable = false, length = 45)
    private String surname;
    @Basic
    @Column(name = "creationDate", nullable = false)
    private Timestamp creationDate;
    @OneToMany(mappedBy = "clientByClientId")
    private Collection<AuthorizedAccountEntity> authorizedAccountsById;
    @OneToMany(mappedBy = "clientByClientId")
    private Collection<BankAccountEntity> bankAccountsById;
    @OneToMany(mappedBy = "clientByClientId")
    private Collection<ChatEntity> chatsById;
    @OneToMany(mappedBy = "clientByClientId")
    private Collection<MessageEntity> messagesById;
    @OneToMany(mappedBy = "clientByClientId")
    private Collection<RequestEntity> requestsById;
    @OneToMany(mappedBy = "clientByClientId")
    private Collection<TransactionEntity> transactionsById;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientEntity client = (ClientEntity) o;

        if (id != client.id) return false;
        if (dni != null ? !dni.equals(client.dni) : client.dni != null) return false;
        if (name != null ? !name.equals(client.name) : client.name != null) return false;
        if (password != null ? !password.equals(client.password) : client.password != null) return false;
        if (salt != null ? !salt.equals(client.salt) : client.salt != null) return false;
        if (surname != null ? !surname.equals(client.surname) : client.surname != null) return false;
        if (creationDate != null ? !creationDate.equals(client.creationDate) : client.creationDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (dni != null ? dni.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        return result;
    }

    public Collection<AuthorizedAccountEntity> getAuthorizedAccountsById() {
        return authorizedAccountsById;
    }

    public void setAuthorizedAccountsById(Collection<AuthorizedAccountEntity> authorizedAccountsById) {
        this.authorizedAccountsById = authorizedAccountsById;
    }

    public Collection<BankAccountEntity> getBankAccountsById() {
        return bankAccountsById;
    }

    public void setBankAccountsById(Collection<BankAccountEntity> bankAccountsById) {
        this.bankAccountsById = bankAccountsById;
    }

    public Collection<ChatEntity> getChatsById() {
        return chatsById;
    }

    public void setChatsById(Collection<ChatEntity> chatsById) {
        this.chatsById = chatsById;
    }

    public Collection<MessageEntity> getMessagesById() {
        return messagesById;
    }

    public void setMessagesById(Collection<MessageEntity> messagesById) {
        this.messagesById = messagesById;
    }

    public Collection<RequestEntity> getRequestsById() {
        return requestsById;
    }

    public void setRequestsById(Collection<RequestEntity> requestsById) {
        this.requestsById = requestsById;
    }

    public Collection<TransactionEntity> getTransactionsById() {
        return transactionsById;
    }

    public void setTransactionsById(Collection<TransactionEntity> transactionsById) {
        this.transactionsById = transactionsById;
    }

    public ClientDTO toDTO() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setDni(getDni());
        clientDTO.setId(getId());
        clientDTO.setName(getName());
        clientDTO.setCreationDate(getCreationDate());
        clientDTO.setSurname(getSurname());
        return  clientDTO;
    }
}
