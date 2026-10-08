package com.example.resqnet.service;

import com.example.resqnet.model.ChatMessage;
import com.example.resqnet.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository repository;

    public ChatService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public ChatMessage saveMessage(ChatMessage message) {
        if (message.getSenderEmail() == null ||
            message.getReceiverEmail() == null ||
            message.getMessage() == null ||
            message.getMessage().isBlank()) {
            throw new IllegalArgumentException(
                    "Sender, receiver and message are required"
            );
        }

        message.setSenderEmail(
                message.getSenderEmail().trim().toLowerCase()
        );
        message.setReceiverEmail(
                message.getReceiverEmail().trim().toLowerCase()
        );
        message.setMessage(message.getMessage().trim());

        return repository.save(message);
    }

    public List<ChatMessage> getConversation(
            String user1,
            String user2) {

        return repository.findConversation(
                user1.toLowerCase(),
                user2.toLowerCase()
        );
    }

    public long getUnreadCount(String email) {
        return repository
                .findByReceiverEmailAndReadFalse(email.toLowerCase())
                .size();
    }
}