package com.taw.polybank.controller.assistance;

import com.taw.polybank.dto.ChatDTO;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.dto.MessageDTO;
import com.taw.polybank.service.ChatService;
import com.taw.polybank.service.ClientService;
import com.taw.polybank.service.EmployeeService;
import com.taw.polybank.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("client/assistance")
public class ClientAssistanceController {

    @Autowired
    protected ClientService clientService;

    @Autowired
    protected ChatService chatService;

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected EmployeeService employeeService;

    @GetMapping(value={"/", ""})
    public String doListChats(Model model, HttpSession session) {
        Optional<ClientDTO> client = this.clientService.findById((Integer) session.getAttribute("clientID"));
        if (client.isPresent()) {
            List<ChatDTO> chatList = chatService.findByClient(client.get());
            model.addAttribute("chatList", chatList);
            return "assistance/clientAssistanceChatList";
        }
        return "error";
    }

    @GetMapping("/chat")
    public String doOpenChat (@RequestParam("id") Integer chatId, Model model) {
        ChatDTO chat = this.chatService.findById(chatId);

        if (chat != null) {
            model.addAttribute("chat", chat);
            model.addAttribute("messageList", messageService.findByChat(chat));

            return "assistance/clientAssistanceChat";
        }

        return "error";
    }

    @PostMapping("/newChat")
    public String doNewChat (Model model, HttpSession session) {
        Optional<ClientDTO> client = this.clientService.findById((Integer) session.getAttribute("clientID"));
        if (client.isPresent()) {
            ChatDTO chat = new ChatDTO();
            chat.setClient(client.get());
            chat.setAssistant(employeeService.findEmployeeWithMinimumChats().get(0));
            chat.setClosed(false);
            this.chatService.save(chat);
            model.addAttribute("chat", chatService.findByMaxId());
            model.addAttribute("messageList", messageService.findByChat(chat));
            return "assistance/clientAssistanceChat";
        }

        return "error";
    }

    @PostMapping("/send")
    public String doSend (@RequestParam("content") String content, @RequestParam("chatId") Integer chatId) {
        ChatDTO chat = chatService.findById(chatId);

        if (chat != null) {
            MessageDTO message = new MessageDTO();
            message.setChat(chat);
            message.setContent(content);
            message.setTimestamp(Timestamp.from(Instant.now()));
            message.setAssistant(null);
            message.setClient(chat.getClient());

            this.messageService.save(message);

            return "redirect:/client/assistance/chat?id=" + chatId;
        }

        return "error";
    }

    @PostMapping("/close")
    public String doSend (@RequestParam("chatId") Integer chatId) {
        ChatDTO chat = chatService.findById(chatId);

        if (chat != null) {
            chat.setClosed(true);

            this.chatService.close(chat);

            return "redirect:/client/assistance/";
        }

        return "error";
    }
}
