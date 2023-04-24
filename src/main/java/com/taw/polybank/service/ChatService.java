package com.taw.polybank.service;

import com.taw.polybank.dao.ChatRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dto.ChatDTO;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.dto.EmployeeDTO;
import com.taw.polybank.entity.ChatEntity;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    @Autowired
    protected ChatRepository chatRepository;
    @Autowired
    protected EmployeeRepository employeeRepository;
    @Autowired
    protected ClientRepository clientRepository;

    public ChatDTO findById(Integer chatId) {
        ChatEntity chatEntity = this.chatRepository.findById(chatId).orElse(null);

        if (chatEntity != null) {
            return chatEntity.toDTO();
        }

        return null;
    }

    public List<ChatDTO> findByClient(ClientDTO client) {
        ClientEntity clientEntity = clientRepository.findById(client.getId()).orElse(null);
        List<ChatDTO> chatList = new ArrayList<>();

        if (clientEntity != null) {
            List<ChatEntity> chatEntityList = chatRepository.findByClient(clientEntity);
            chatList = this.listToDTO(chatEntityList);
        }

        return chatList;
    }

    public List<ChatDTO> findByEmployee(EmployeeDTO employee) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employee.getId()).orElse(null);
        List<ChatDTO> chatList = new ArrayList<>();

        if (employeeEntity != null) {
            List<ChatEntity> chatEntityList = chatRepository.findByEmployee(employeeEntity);
            chatList = this.listToDTO(chatEntityList);
        }

        return chatList;
    }

    public List<ChatDTO> findByEmployeeAndClientDni(EmployeeDTO employee, String clientDni) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employee.getId()).orElse(null);
        List<ChatDTO> chatList = new ArrayList<>();

        if (employeeEntity != null) {
            List<ChatEntity> chatEntityList = chatRepository.findByEmployeeAndClientDni(employeeEntity, clientDni);
            chatList = this.listToDTO(chatEntityList);
        }

        return chatList;
    }

    public List<ChatDTO> findByEmployeeAndClientDniAndClientName(EmployeeDTO employee, String clientDni, String clientName) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employee.getId()).orElse(null);
        List<ChatDTO> chatList = new ArrayList<>();

        if (employeeEntity != null) {
            List<ChatEntity> chatEntityList = chatRepository.findByEmployeeAndClientDniAndClientName(employeeEntity, clientDni, clientName);
            chatList = this.listToDTO(chatEntityList);
        }

        return chatList;
    }

    public List<ChatDTO> findByEmployeeAndClientDniAndRecent(EmployeeDTO employee, String clientDni) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employee.getId()).orElse(null);
        List<ChatDTO> chatList = new ArrayList<>();

        if (employeeEntity != null) {
            List<ChatEntity> chatEntityList = chatRepository.findByEmployeeAndClientDniAndRecent(employeeEntity, clientDni);
            chatList = this.listToDTO(chatEntityList);
        }

        return chatList;
    }

    public List<ChatDTO> findByEmployeeAndClientDniAndClientNameAndRecent(EmployeeDTO employee, String clientDni, String clientName) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employee.getId()).orElse(null);
        List<ChatDTO> chatList = new ArrayList<>();

        if (employeeEntity != null) {
            List<ChatEntity> chatEntityList = chatRepository.findByEmployeeAndClientDniAndClientNameAndRecent(employeeEntity, clientDni, clientName);
            chatList = this.listToDTO(chatEntityList);
        }

        return chatList;
    }

    public List<ChatDTO> findByEmployeeAndClientName(EmployeeDTO employee, String clientName) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employee.getId()).orElse(null);
        List<ChatDTO> chatList = new ArrayList<>();

        if (employeeEntity != null) {
            List<ChatEntity> chatEntityList = chatRepository.findByEmployeeAndClientName(employeeEntity, clientName);
            chatList = this.listToDTO(chatEntityList);
        }

        return chatList;
    }

    public List<ChatDTO> findByEmployeeAndClientNameAndRecent(EmployeeDTO employee, String clientName) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employee.getId()).orElse(null);
        List<ChatDTO> chatList = new ArrayList<>();

        if (employeeEntity != null) {
            List<ChatEntity> chatEntityList = chatRepository.findByEmployeeAndClientNameAndRecent(employeeEntity, clientName);
            chatList = this.listToDTO(chatEntityList);
        }

        return chatList;
    }

    public List<ChatDTO> findByEmployeeAndRecent(EmployeeDTO employee) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employee.getId()).orElse(null);
        List<ChatDTO> chatList = new ArrayList<>();

        if (employeeEntity != null) {
            List<ChatEntity> chatEntityList = chatRepository.findByEmployeeAndRecent(employeeEntity);
            chatList = this.listToDTO(chatEntityList);
        }

        return chatList;
    }

    public void save(ChatDTO chat) {
        ChatEntity chatEntity = new ChatEntity();

        chatEntity.setClientByClientId(clientRepository.findById(chat.getClient().getId()).orElse(null));
        chatEntity.setEmployeeByAssistantId(employeeRepository.findById(chat.getAssistant().getId()).orElse(null));
        chatEntity.setMessagesById(new ArrayList<>());
        chatEntity.setClosed((byte) ((chat.isClosed())? 1 : 0));

        this.chatRepository.save(chatEntity);
    }

    public void close(ChatDTO chat) {
        ChatEntity chatEntity = chatRepository.findById(chat.getId()).orElse(null);

        if (chat != null) {
            chatEntity.setClosed((byte) ((chat.isClosed())? 1 : 0));

            chatRepository.save(chatEntity);
        }
    }

    protected List<ChatDTO> listToDTO(List<ChatEntity> chatEntityList) {
        ArrayList chatList = new ArrayList<ChatDTO>();
        chatEntityList.forEach((final ChatEntity chatEntity) -> chatList.add(chatEntity.toDTO()));

        return chatList;
    }
}
