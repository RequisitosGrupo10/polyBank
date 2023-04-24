package com.taw.polybank.entity;

import com.taw.polybank.dto.MessageDTO;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "Message", schema = "polyBank", catalog = "")
public class MessageEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "content", nullable = false, length = 1000)
    private String content;
    @Basic
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;
    @ManyToOne
    @JoinColumn(name = "Chat_id", referencedColumnName = "id", nullable = false)
    private ChatEntity chatByChatId;
    @ManyToOne
    @JoinColumn(name = "Employee_id", referencedColumnName = "id", nullable = true)
    private EmployeeEntity employeeByEmployeeId;
    @ManyToOne
    @JoinColumn(name = "Client_id", referencedColumnName = "id", nullable = true)
    private ClientEntity clientByClientId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageEntity that = (MessageEntity) o;

        if (id != that.id) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    public ChatEntity getChatByChatId() {
        return chatByChatId;
    }

    public void setChatByChatId(ChatEntity chatByChatId) {
        this.chatByChatId = chatByChatId;
    }

    public EmployeeEntity getEmployeeByEmployeeId() {
        return employeeByEmployeeId;
    }

    public void setEmployeeByEmployeeId(EmployeeEntity employeeByEmployeeId) {
        this.employeeByEmployeeId = employeeByEmployeeId;
    }

    public ClientEntity getClientByClientId() {
        return clientByClientId;
    }

    public void setClientByClientId(ClientEntity clientByClientId) {
        this.clientByClientId = clientByClientId;
    }

    public MessageDTO toDTO(){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(getId());
        messageDTO.setContent(getContent());
        messageDTO.setTimestamp(getTimestamp());
        messageDTO.setChat(getChatByChatId().toDTO());
        messageDTO.setAssistant(getEmployeeByEmployeeId().toDTO());
        messageDTO.setClient(getClientByClientId().toDTO());
        return messageDTO;
    }
}
