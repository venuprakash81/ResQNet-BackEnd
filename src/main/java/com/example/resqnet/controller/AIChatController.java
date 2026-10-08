package com.example.resqnet.controller;

import com.example.resqnet.service.AIChatService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "https://res-q-net-front-end-venu-prakash.vercel.app")
public class AIChatController {

    private final AIChatService aiChatService;

    public AIChatController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(
            @RequestBody Map<String, String> request) {

        String message = request.get("message");
        String context = request.getOrDefault("context", "");

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                "Message cannot be empty"
            );
        }

        String answer = aiChatService.askAI(message, context);

        return Map.of("answer", answer);
    }
}
