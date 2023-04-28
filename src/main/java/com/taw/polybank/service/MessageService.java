package com.taw.polybank.service;

import com.taw.polybank.dao.ChatRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dao.MessageRepository;
import com.taw.polybank.dto.ChatDTO;
import com.taw.polybank.dto.MessageDTO;
import com.taw.polybank.entity.ChatEntity;
import com.taw.polybank.entity.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    protected MessageRepository messageRepository;
    @Autowired
    protected ChatRepository chatRepository;
    @Autowired
    protected EmployeeRepository employeeRepository;
    @Autowired
    protected ClientRepository clientRepository;

    public List<MessageDTO> findByChat(ChatDTO chat) {
        ChatEntity chatEntity = chatRepository.findById(chat.getId()).orElse(null);
        List<MessageDTO> messageList = new ArrayList<>();

        if (chatEntity != null) {
            List<MessageEntity> messageEntityList = messageRepository.findByChat(chatEntity);
            messageList = this.listToDTO(messageEntityList);
        }

        return messageList;
    }

    public void save(MessageDTO message) {
        MessageEntity messageEntity = new MessageEntity();

        messageEntity.setChatByChatId(chatRepository.findById(message.getChat().getId()).orElse(null));
        messageEntity.setContent(message.getContent());
        messageEntity.setTimestamp(message.getTimestamp());

        if (message.getAssistant() == null) {
            messageEntity.setEmployeeByEmployeeId(null);
        } else {
            messageEntity.setEmployeeByEmployeeId(employeeRepository.findById(message.getAssistant().getId()).orElse(null));
        }

        if (message.getClient() == null) {
            messageEntity.setClientByClientId(null);
        } else {
            messageEntity.setClientByClientId(clientRepository.findById(message.getClient().getId()).orElse(null));
        }

        this.messageRepository.save(messageEntity);
    }

    protected List<MessageDTO> listToDTO(List<MessageEntity> messageEntityList) {
        ArrayList messageList = new ArrayList<MessageDTO>();
        for (MessageEntity messageEntity : messageEntityList) {
            messageList.add(messageEntity.toDTO());
        }
        return messageList;
    }
}
