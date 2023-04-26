package com.taw.polybank.controller.assistence;

import com.taw.polybank.dao.ChatRepository;
import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dao.MessageRepository;
import com.taw.polybank.entity.ChatEntity;
import com.taw.polybank.entity.EmployeeEntity;
import com.taw.polybank.entity.MessageEntity;
import com.taw.polybank.ui.AssistantFilter;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("employee/assistence")
public class AssistantController {

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected ChatRepository chatRepository;

    @Autowired
    protected MessageRepository messageRepository;

    @GetMapping("/")
    public String doListChats(Model model, HttpSession session) {
        return processFilter(model, session, null);
    }

    @PostMapping("/filter")
    public String doFilterChats(Model model, HttpSession session, @ModelAttribute("filter")AssistantFilter filter) {
        return processFilter(model, session, filter);
    }

    protected String processFilter(Model model, HttpSession session, AssistantFilter filter) {
        List<ChatEntity> chatList;
        EmployeeEntity employee = this.employeeRepository.findById((Integer) session.getAttribute("employeeID")).orElse(null);

        if (employee != null) {
            if (filter == null || (filter.getClientDni() == "" && filter.getClientName() == "" && filter.getRecent() == false)) {
                chatList = (List<ChatEntity>) employee.getChatsById();
                filter = new AssistantFilter();
            } else {
                if (filter.getClientDni() != "") {
                    if (filter.getClientName() == "" && filter.getRecent() == false) {
                        chatList = this.chatRepository.findByEmployeeAndClientDni(employee, filter.getClientDni());
                    } else if (filter.getClientName() != "" && filter.getRecent() == false) {
                        chatList = this.chatRepository.findByEmployeeAndClientDniAndClientName(employee, filter.getClientDni(), filter.getClientName());
                    } else if (filter.getClientName() == "" && filter.getRecent() == true) {
                        chatList = this.chatRepository.findByEmployeeAndClientDniAndRecent(employee, filter.getClientName());
                    } else {
                        chatList = this.chatRepository.findByEmployeeAndClientDniAndClientNameAndRecent(employee, filter.getClientDni(), filter.getClientName());
                    }
                } else if (filter.getClientName() != "") {
                    if (filter.getRecent() == false) {
                        chatList = this.chatRepository.findByEmployeeAndClientName(employee, filter.getClientName());
                    } else {
                        chatList = this.chatRepository.findByEmployeeAndClientNameAndRecent(employee, filter.getClientName());
                    }
                } else {
                    chatList = this.chatRepository.findByEmployeeAndRecent(employee);
                }
            }
            model.addAttribute("chatList", chatList);
            model.addAttribute("filter", filter);

            return "assistence/assistantChatList";
        }

        return "error";
    }

    @GetMapping("/chat")
    public String doOpenChat (@RequestParam("id") Integer idChat, Model model) {
        ChatEntity chat = this.chatRepository.findById(idChat).orElse(null);
        model.addAttribute("chat", chat);

        return "assistence/assistantChat";
    }

    @PostMapping("/send")
    public String doSend (@RequestParam("content") String content, @RequestParam("chatId") Integer chatId) {
        ChatEntity chat = chatRepository.findById(chatId).orElse(null);
        MessageEntity message = new MessageEntity();
        message.setChatByChatId(chat);
        message.setContent(content);
        message.setTimestamp(Timestamp.from(Instant.now()));
        message.setEmployeeByEmployeeId(chat.getEmployeeByAssistantId());
        message.setClientByClientId(null);
        this.messageRepository.save(message);
        return "redirect:/employee/assistence/chat?id=" + chatId;
    }
}
