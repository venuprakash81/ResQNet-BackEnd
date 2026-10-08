package com.example.resqnet.controller;

import com.example.resqnet.model.ChatMessage;
import com.example.resqnet.service.ChatService;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(
            ChatService chatService,
            SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage message) {

        ChatMessage saved =
                chatService.saveMessage(message);

        String room = getRoom(
                saved.getSenderEmail(),
                saved.getReceiverEmail()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + room,
                saved
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(
            @RequestParam String user1,
            @RequestParam String user2) {

        return ResponseEntity.ok(
                chatService.getConversation(user1, user2)
        );
    }

    @GetMapping("/unread/{email}")
    public ResponseEntity<Map<String, Long>> getUnread(
            @PathVariable String email) {

        return ResponseEntity.ok(
                Map.of("unread",
                        chatService.getUnreadCount(email))
        );
    }

    private String getRoom(String email1, String email2) {
        return email1.compareToIgnoreCase(email2) < 0
                ? email1 + "__" + email2
                : email2 + "__" + email1;
    }
}