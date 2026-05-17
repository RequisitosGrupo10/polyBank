package com.taw.polybank.service

import com.taw.polybank.dao.ChatRepository
import com.taw.polybank.dao.ClientRepository
import com.taw.polybank.dao.EmployeeRepository
import com.taw.polybank.dto.ChatDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.dto.EmployeeDTO
import com.taw.polybank.entity.ChatEntity
import com.taw.polybank.entity.MessageEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author Javier Jordán Luque
 */
@Service
class ChatService(
  private var chatRepository: ChatRepository,
  private var employeeRepository: EmployeeRepository,
  private var clientRepository: ClientRepository,
) {
  fun findById(chatId: Int): ChatDTO? {
    val chatEntity = this.chatRepository.findById(chatId).orElse(null)

    if (chatEntity != null) {
      return chatEntity.toDTO()
    }

    return null
  }

  fun findByClient(client: ClientDTO): List<ChatDTO> {
    val clientEntity = clientRepository.findById(client.getId()).orElse(null)
    var chatList: List<ChatDTO> = ArrayList()

    if (clientEntity != null) {
      val chatEntityList = chatRepository.findByClient(clientEntity)
      chatList = this.listToDTO(chatEntityList)
    }

    return chatList
  }

  fun findByEmployee(employee: EmployeeDTO): List<ChatDTO> {
    val employeeEntity = employeeRepository.findById(employee.getId()).orElse(null)
    var chatList: List<ChatDTO> = ArrayList()

    if (employeeEntity != null) {
      val chatEntityList = chatRepository.findByEmployee(employeeEntity)
      chatList = this.listToDTO(chatEntityList)
    }

    return chatList
  }

  fun findByEmployeeAndClientDni(employee: EmployeeDTO, clientDni: String?): List<ChatDTO> {
    val employeeEntity = employeeRepository.findById(employee.getId()).orElse(null)
    var chatList: List<ChatDTO> = ArrayList()

    if (employeeEntity != null) {
      val chatEntityList =
        chatRepository.findByEmployeeAndClientDni(employeeEntity, clientDni)
      chatList = this.listToDTO(chatEntityList)
    }

    return chatList
  }

  fun findByEmployeeAndClientDniAndClientName(
    employee: EmployeeDTO,
    clientDni: String?,
    clientName: String?,
  ): List<ChatDTO> {
    val employeeEntity = employeeRepository.findById(employee.getId()).orElse(null)
    var chatList: List<ChatDTO> = ArrayList()

    if (employeeEntity != null) {
      val chatEntityList =
        chatRepository.findByEmployeeAndClientDniAndClientName(
          employeeEntity,
          clientDni,
          clientName,
        )
      chatList = this.listToDTO(chatEntityList)
    }

    return chatList
  }

  fun findByEmployeeAndClientDniAndRecent(employee: EmployeeDTO, clientDni: String?): List<ChatDTO> {
    val employeeEntity = employeeRepository.findById(employee.getId()).orElse(null)
    var chatList: List<ChatDTO> = ArrayList()

    if (employeeEntity != null) {
      val chatEntityList =
        chatRepository.findByEmployeeAndClientDniAndRecent(employeeEntity, clientDni)
      chatList = this.listToDTO(chatEntityList)
    }

    return chatList
  }

  fun findByEmployeeAndClientDniAndClientNameAndRecent(
    employee: EmployeeDTO,
    clientDni: String?,
    clientName: String?,
  ): List<ChatDTO> {
    val employeeEntity = employeeRepository.findById(employee.getId()).orElse(null)
    var chatList: List<ChatDTO> = ArrayList()

    if (employeeEntity != null) {
      val chatEntityList =
        chatRepository!!.findByEmployeeAndClientDniAndClientNameAndRecent(
          employeeEntity,
          clientDni,
          clientName,
        )
      chatList = this.listToDTO(chatEntityList)
    }

    return chatList
  }

  fun findByEmployeeAndClientName(employee: EmployeeDTO, clientName: String?): List<ChatDTO> {
    val employeeEntity = employeeRepository.findById(employee.getId()).orElse(null)
    var chatList: List<ChatDTO> = ArrayList()

    if (employeeEntity != null) {
      val chatEntityList =
        chatRepository.findByEmployeeAndClientName(employeeEntity, clientName)
      chatList = this.listToDTO(chatEntityList)
    }

    return chatList
  }

  fun findByEmployeeAndClientNameAndRecent(
    employee: EmployeeDTO,
    clientName: String?,
  ): List<ChatDTO> {
    val employeeEntity = employeeRepository.findById(employee.getId()).orElse(null)
    var chatList: List<ChatDTO> = ArrayList()

    if (employeeEntity != null) {
      val chatEntityList =
        chatRepository.findByEmployeeAndClientNameAndRecent(employeeEntity, clientName)
      chatList = this.listToDTO(chatEntityList)
    }

    return chatList
  }

  fun findByEmployeeAndRecent(employee: EmployeeDTO): List<ChatDTO> {
    val employeeEntity = employeeRepository.findById(employee.getId()).orElse(null)
    var chatList: List<ChatDTO> = ArrayList()

    if (employeeEntity != null) {
      val chatEntityList = chatRepository.findByEmployeeAndRecent(employeeEntity)
      chatList = this.listToDTO(chatEntityList)
    }

    return chatList
  }

  fun findByMaxId(): ChatDTO? {
    val chatEntity = chatRepository.findByMaxId().get(0)
    val chat = chatEntity.toDTO()
    return chat
  }

  fun save(chat: ChatDTO) {
    val chatEntity = ChatEntity()

    chatEntity.setClientByClientId(
      clientRepository.findById(chat.getClient().getId()).orElse(null),
    )
    chatEntity.setEmployeeByAssistantId(
      employeeRepository.findById(chat.getAssistant().getId()).orElse(null),
    )
    chatEntity.setMessagesById(ArrayList<MessageEntity?>())
    chatEntity.setClosed((if (chat.isClosed()) 1 else 0).toByte())

    this.chatRepository.save<ChatEntity>(chatEntity)
  }

  fun close(chat: ChatDTO) {
    val chatEntity = chatRepository.findById(chat.getId()).orElse(null)

    if (chat != null) {
      chatEntity!!.setClosed((if (chat.isClosed()) 1 else 0).toByte())

      chatRepository.save<ChatEntity>(chatEntity)
    }
  }

  protected fun listToDTO(chatEntityList: List<ChatEntity>): List<ChatDTO> {
    val chatList: ArrayList<ChatDTO> = ArrayList()
    for (chatEntity in chatEntityList) {
      chatList.add(chatEntity.toDTO())
    }
    return chatList
  }
}
