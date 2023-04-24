package com.taw.polybank.entity;

import com.taw.polybank.dto.EmployeeDTO;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Employee", schema = "polyBank", catalog = "")
public class EmployeeEntity {
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
    @Column(name = "type", nullable = false)
    private Object type;
    @Basic
    @Column(name = "salt", nullable = true, length = 32)
    private String salt;
    @OneToMany(mappedBy = "employeeByAssistantId")
    private Collection<ChatEntity> chatsById;
    @OneToMany(mappedBy = "employeeByEmployeeId")
    private Collection<MessageEntity> messagesById;
    @OneToMany(mappedBy = "employeeByEmployeeId")
    private Collection<RequestEntity> requestsById;

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

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployeeEntity that = (EmployeeEntity) o;

        if (id != that.id) return false;
        if (dni != null ? !dni.equals(that.dni) : that.dni != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (salt != null ? !salt.equals(that.salt) : that.salt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (dni != null ? dni.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        return result;
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

    public EmployeeDTO toDTO() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(getId());
        employeeDTO.setDni(getDni());
        employeeDTO.setName(getName());
        employeeDTO.setType(getType());
        return employeeDTO;
    }
}
