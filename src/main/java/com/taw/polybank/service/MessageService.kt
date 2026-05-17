package com.taw.polybank.service

import com.taw.polybank.dao.ChatRepository
import com.taw.polybank.dao.ClientRepository
import com.taw.polybank.dao.EmployeeRepository
import com.taw.polybank.dao.MessageRepository
import com.taw.polybank.dto.ChatDTO
import com.taw.polybank.dto.MessageDTO
import com.taw.polybank.entity.MessageEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author Javier Jordán Luque
 */
@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val employeeRepository: EmployeeRepository,
    private val clientRepository: ClientRepository,
) {
    fun findByChat(chat: ChatDTO): MutableList<MessageDTO?> {
        val chatEntity = chatRepository.findById(chat.getId()).orElse(null)
        var messageList: MutableList<MessageDTO?> = ArrayList()

        if (chatEntity != null) {
            val messageEntityList = messageRepository.findByChat(chatEntity)
            messageList = this.listToDTO(messageEntityList)
        }

        return messageList
    }

    fun save(message: MessageDTO) {
        val messageEntity = MessageEntity()

        messageEntity.setChatByChatId(chatRepository.findById(message.getChat().getId()).orElse(null))
        messageEntity.setContent(message.getContent())
        messageEntity.setTimestamp(message.getTimestamp())

        if (message.getAssistant() == null) {
            messageEntity.setEmployeeByEmployeeId(null)
        } else {
            messageEntity.setEmployeeByEmployeeId(
                employeeRepository.findById(message.getAssistant().getId()).orElse(null),
            )
        }

        if (message.getClient() == null) {
            messageEntity.setClientByClientId(null)
        } else {
            messageEntity.setClientByClientId(
                clientRepository.findById(message.getClient().getId()).orElse(null),
            )
        }

        this.messageRepository.save<MessageEntity>(messageEntity)
    }

    fun listToDTO(messageEntityList: MutableList<MessageEntity>): MutableList<MessageDTO?> {
        val messageList = ArrayList<MessageDTO?>()
        for (messageEntity in messageEntityList) {
            messageList.add(messageEntity.toDTO())
        }
        return messageList
    }
}
