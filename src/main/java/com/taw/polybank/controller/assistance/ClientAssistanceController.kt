package com.taw.polybank.controller.assistance

import com.taw.polybank.dto.ChatDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.dto.MessageDTO
import com.taw.polybank.service.ChatService
import com.taw.polybank.service.ClientService
import com.taw.polybank.service.EmployeeService
import com.taw.polybank.service.MessageService
import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.sql.Timestamp
import java.time.Instant

/**
 * @author Javier Jordán Luque
 */
@Controller
@RequestMapping("client/assistance")
class ClientAssistanceController {
    @Autowired
    protected var clientService: ClientService? = null

    @Autowired
    protected var chatService: ChatService? = null

    @Autowired
    protected var messageService: MessageService? = null

    @Autowired
    protected var employeeService: EmployeeService? = null

    @GetMapping(value = ["/", ""])
    fun doListChats(
        model: Model,
        session: HttpSession,
    ): String {
        val client = this.clientService!!.findById((session.getAttribute("client") as ClientDTO).getId())
        if (client.isPresent()) {
            val chatList = chatService!!.findByClient(client.get())
            model.addAttribute("chatList", chatList)
            return "assistance/clientAssistanceChatList"
        }
        return "error"
    }

    @GetMapping("/chat")
    fun doOpenChat(
        @RequestParam("id") chatId: Int?,
        model: Model,
    ): String {
        val chat = this.chatService!!.findById(chatId)

        if (chat != null) {
            model.addAttribute("chat", chat)
            model.addAttribute("messageList", messageService!!.findByChat(chat))

            return "assistance/clientAssistanceChat"
        }

        return "error"
    }

    @PostMapping("/newChat")
    fun doNewChat(
        model: Model,
        session: HttpSession,
    ): String {
        val client = this.clientService!!.findById((session.getAttribute("client") as ClientDTO).getId())
        if (client.isPresent()) {
            val chat = ChatDTO()
            chat.setClient(client.get())
            chat.setAssistant(employeeService!!.findEmployeeWithMinimumChats().get(0))
            chat.setClosed(false)
            this.chatService!!.save(chat)
            model.addAttribute("chat", chatService!!.findByMaxId())
            model.addAttribute("messageList", messageService!!.findByChat(chat))
            return "assistance/clientAssistanceChat"
        }

        return "error"
    }

    @PostMapping("/send")
    fun doSend(
        @RequestParam("content") content: String?,
        @RequestParam("chatId") chatId: Int?,
    ): String {
        val chat = chatService!!.findById(chatId)

        if (chat != null) {
            val message = MessageDTO()
            message.setChat(chat)
            message.setContent(content)
            message.setTimestamp(Timestamp.from(Instant.now()))
            message.setAssistant(null)
            message.setClient(chat.getClient())

            this.messageService!!.save(message)

            return "redirect:/client/assistance/chat?id=" + chatId
        }

        return "error"
    }

    @PostMapping("/close")
    fun doSend(
        @RequestParam("chatId") chatId: Int?,
    ): String {
        val chat = chatService!!.findById(chatId)

        if (chat != null) {
            chat.setClosed(true)

            this.chatService!!.close(chat)

            return "redirect:/client/assistance/"
        }

        return "error"
    }
}
