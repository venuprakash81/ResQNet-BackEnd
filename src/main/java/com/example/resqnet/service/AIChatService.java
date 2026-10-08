package com.example.resqnet.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Service
public class AIChatService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String AI_URL = "http://localhost:8000/chat";

    public String askAI(String message, String context) {
        Map<String, String> request = Map.of(
            "message", message,
            "context", context == null ? "" : context
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity =
            new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            AI_URL,
            HttpMethod.POST,
            entity,
            Map.class
        );

        Object answer = response.getBody().get("answer");
        return answer == null ? "No answer received." : answer.toString();
    }
}