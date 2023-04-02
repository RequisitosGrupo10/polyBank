package com.taw.polybank.entity;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "Chat", schema = "polyBank", catalog = "")
public class ChatEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "closed")
    private byte closed;
    @ManyToOne
    @JoinColumn(name = "Client_id", referencedColumnName = "id", nullable = false)
    private ClientEntity clientByClientId;
    @ManyToOne
    @JoinColumn(name = "Assistant_id", referencedColumnName = "id", nullable = false)
    private EmployeeEntity employeeByAssistantId;
    @OneToMany(mappedBy = "chatByChatId")
    private Collection<MessageEntity> messagesById;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getClosed() {
        return closed;
    }

    public void setClosed(byte closed) {
        this.closed = closed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatEntity that = (ChatEntity) o;
        return id == that.id && closed == that.closed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, closed);
    }

    public ClientEntity getClientByClientId() {
        return clientByClientId;
    }

    public void setClientByClientId(ClientEntity clientByClientId) {
        this.clientByClientId = clientByClientId;
    }

    public EmployeeEntity getEmployeeByAssistantId() {
        return employeeByAssistantId;
    }

    public void setEmployeeByAssistantId(EmployeeEntity employeeByAssistantId) {
        this.employeeByAssistantId = employeeByAssistantId;
    }

    public Collection<MessageEntity> getMessagesById() {
        return messagesById;
    }

    public void setMessagesById(Collection<MessageEntity> messagesById) {
        this.messagesById = messagesById;
    }
}
