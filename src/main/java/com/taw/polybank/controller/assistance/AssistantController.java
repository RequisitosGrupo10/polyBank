package com.taw.polybank.controller.assistance;

import com.taw.polybank.dto.ChatDTO;
import com.taw.polybank.dto.EmployeeDTO;
import com.taw.polybank.dto.MessageDTO;
import com.taw.polybank.entity.EmployeeEntity;
import com.taw.polybank.service.ChatService;
import com.taw.polybank.service.EmployeeService;
import com.taw.polybank.service.MessageService;
import com.taw.polybank.ui.assistence.AssistantFilter;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("employee/assistance")
public class AssistantController {

    @Autowired
    protected EmployeeService employeeService;

    @Autowired
    protected ChatService chatService;

    @Autowired
    protected MessageService messageService;

    @GetMapping(value={"/", ""})
    public String doListChats(Model model, HttpSession session) {
        return processFilter(model, session, null);
    }

    @PostMapping("/filter")
    public String doFilterChats(Model model, HttpSession session, @ModelAttribute("filter")AssistantFilter filter) {
        return processFilter(model, session, filter);
    }

    protected String processFilter(Model model, HttpSession session, AssistantFilter filter) {
        List<ChatDTO> chatList;
        EmployeeDTO employee = this.employeeService.findById(((EmployeeEntity) session.getAttribute("employee")).getId());

        if (employee != null) {
            if (filter == null || (filter.getClientDni() == "" && filter.getClientName() == "" && filter.getRecent() == false)) {
                chatList = this.chatService.findByEmployee(employee);
                filter = new AssistantFilter();
            } else {
                if (filter.getClientDni() != "") {
                    if (filter.getClientName() == "" && filter.getRecent() == false) {
                        chatList = this.chatService.findByEmployeeAndClientDni(employee, filter.getClientDni());
                    } else if (filter.getClientName() != "" && filter.getRecent() == false) {
                        chatList = this.chatService.findByEmployeeAndClientDniAndClientName(employee, filter.getClientDni(), filter.getClientName());
                    } else if (filter.getClientName() == "" && filter.getRecent() == true) {
                        chatList = this.chatService.findByEmployeeAndClientDniAndRecent(employee, filter.getClientName());
                    } else {
                        chatList = this.chatService.findByEmployeeAndClientDniAndClientNameAndRecent(employee, filter.getClientDni(), filter.getClientName());
                    }
                } else if (filter.getClientName() != "") {
                    if (filter.getRecent() == false) {
                        chatList = this.chatService.findByEmployeeAndClientName(employee, filter.getClientName());
                    } else {
                        chatList = this.chatService.findByEmployeeAndClientNameAndRecent(employee, filter.getClientName());
                    }
                } else {
                    chatList = this.chatService.findByEmployeeAndRecent(employee);
                }
            }
            model.addAttribute("chatList", chatList);
            model.addAttribute("filter", filter);

            return "assistance/assistantChatList";
        }

        return "error";
    }

    @GetMapping("/chat")
    public String doOpenChat (@RequestParam("id") Integer chatId, Model model) {
        ChatDTO chat = this.chatService.findById(chatId);
        if (chat != null) {
            model.addAttribute("chat", chat);
            model.addAttribute("messageList", messageService.findByChat(chat));

            return "assistance/assistantChat";
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
            message.setAssistant(chat.getAssistant());
            message.setClient(null);

            this.messageService.save(message);

            return "redirect:/employee/assistance/chat?id=" + chatId;
        }

        return "error";
    }
}
