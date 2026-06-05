package com.interviewprep.backend.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * AI generation service backed by Groq (cloud) instead of local Ollama.
 * Set GROQ_API_KEY as an environment variable in your deployment.
 */
@Service
public class OllamaService {

    private static final String GROQ_URL =
        "https://api.groq.com/openai/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey;
    private final String modelName;

    public OllamaService(
        @Value("${groq.api.key:}") String apiKey,
        @Value("${groq.model:llama3-8b-8192}") String modelName
    ) {
        this.apiKey = apiKey;
        this.modelName = modelName;
    }

    public String generate(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "AI service is not configured. Please set the GROQ_API_KEY environment variable.";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
            "model",
            modelName,
            "messages",
            List.of(Map.of("role", "user", "content", prompt)),
            "max_tokens",
            512
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(
            body,
            headers
        );

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                GROQ_URL,
                request,
                Map.class
            );
            Map responseBody = response.getBody();
            if (responseBody != null) {
                List choices = (List) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map choice = (Map) choices.get(0);
                    Map message = (Map) choice.get("message");
                    return (String) message.get("content");
                }
            }
            return "No response from AI.";
        } catch (Exception e) {
            return "AI service unavailable: " + e.getMessage();
        }
    }
}
