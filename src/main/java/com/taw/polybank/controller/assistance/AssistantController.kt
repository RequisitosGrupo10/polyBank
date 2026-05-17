package com.taw.polybank.controller.assistance

import com.taw.polybank.dto.ChatDTO
import com.taw.polybank.dto.MessageDTO
import com.taw.polybank.entity.EmployeeEntity
import com.taw.polybank.service.ChatService
import com.taw.polybank.service.EmployeeService
import com.taw.polybank.service.MessageService
import com.taw.polybank.ui.assistence.AssistantFilter
import jakarta.servlet.http.HttpSession
import org.jetbrains.annotations.NotNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.sql.Timestamp
import java.time.Instant

/**
 * @author Javier Jordán Luque
 */
@Controller
@RequestMapping("employee/assistance")
class AssistantController(
  private val employeeService: EmployeeService,
  private val chatService: ChatService,
  private val messageService: MessageService,
) {
  @GetMapping(value = ["/", ""])
  fun doListChats(
    model: Model,
    session: HttpSession,
  ): String = processFilter(model, session, null)

  @PostMapping("/filter")
  fun doFilterChats(
    model: Model,
    session: HttpSession,
    @ModelAttribute("filter") filter: AssistantFilter?,
  ): String = processFilter(model, session, filter)

  protected fun processFilter(
    model: Model,
    session: HttpSession,
    filter: AssistantFilter?,
  ): String {
    var filter = filter
    val chatList: List<ChatDTO>?
    val employee =
      this.employeeService.findById((session.getAttribute("employee") as EmployeeEntity).getId())

    if (employee != null) {
      if (filter == null ||
        (filter.getClientDni() === "" && filter.getClientName() === "" && filter.getRecent() == false)
      ) {
        chatList = this.chatService.findByEmployee(employee)
        filter = AssistantFilter()
      } else {
        if (filter.getClientDni() !== "") {
          if (filter.getClientName() === "" && filter.getRecent() == false) {
            chatList = this.chatService.findByEmployeeAndClientDni(employee, filter.getClientDni())
          } else if (filter.getClientName() !== "" && filter.getRecent() == false) {
            chatList =
              this.chatService.findByEmployeeAndClientDniAndClientName(
                employee,
                filter.getClientDni(),
                filter.getClientName(),
              )
          } else if (filter.getClientName() === "" && filter.getRecent() == true) {
            chatList =
              this.chatService.findByEmployeeAndClientDniAndRecent(
                employee,
                filter.getClientDni(),
              )
          } else {
            chatList =
              this.chatService.findByEmployeeAndClientDniAndClientNameAndRecent(
                employee,
                filter.getClientDni(),
                filter.getClientName(),
              )
          }
        } else if (filter.getClientName() !== "") {
          if (filter.getRecent() == false) {
            chatList =
              this.chatService.findByEmployeeAndClientName(employee, filter.getClientName())
          } else {
            chatList =
              this.chatService.findByEmployeeAndClientNameAndRecent(
                employee,
                filter.getClientName(),
              )
          }
        } else {
          chatList = this.chatService.findByEmployeeAndRecent(employee)
        }
      }
      model.addAttribute("chatList", chatList)
      model.addAttribute("filter", filter)

      return "assistance/assistantChatList"
    }

    return "error"
  }

  @GetMapping("/chat")
  fun doOpenChat(
    @RequestParam("id") @NotNull chatId: Int,
    model: Model,
  ): String {
    val chat = this.chatService.findById(chatId)
    if (chat != null) {
      model.addAttribute("chat", chat)
      model.addAttribute("messageList", messageService.findByChat(chat))

      return "assistance/assistantChat"
    }

    return "error"
  }

  @PostMapping("/send")
  fun doSend(
    @RequestParam("content") content: String?,
    @RequestParam("chatId") @NotNull chatId: Int,
  ): String {
    val chat = chatService.findById(chatId)

    if (chat != null) {
      val message = MessageDTO()
      message.setChat(chat)
      message.setContent(content)
      message.setTimestamp(Timestamp.from(Instant.now()))
      message.setAssistant(chat.getAssistant())
      message.setClient(null)

      this.messageService.save(message)

      return "redirect:/employee/assistance/chat?id=" + chatId
    }

    return "error"
  }
}
