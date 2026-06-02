package com.interviewprep.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QuestionGeneratorService {

    private final String ollamaApiUrl;
    private final String modelName;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuestionGeneratorService(
            @Value("${ollama.api.url:http://localhost:11434/api/generate}") String ollamaApiUrl,
            @Value("${ollama.model:mistral}") String modelName,
            @Value("${ollama.timeout.ms:30000}") int timeoutMs) {
        this.ollamaApiUrl = ollamaApiUrl;
        this.modelName = modelName;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public Map<String, String> generate(String topic, String difficulty) {
        try {
            String prompt = buildPrompt(topic, difficulty);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            requestBody.put("options", Map.of(
                    "temperature", 0.9,
                    "top_p", 0.95,
                    "repeat_penalty", 1.15,
                    "seed", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)
            ));

            String response = restTemplate.postForObject(ollamaApiUrl, requestBody, String.class);
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode responseNode = jsonNode.get("response");
                if (responseNode != null && !responseNode.asText().isBlank()) {
                    return parseGeneratedQuestion(responseNode.asText());
                }
            }
        } catch (RestClientException e) {
            return buildOllamaUnavailableResponse();
        } catch (Exception e) {
            return buildOllamaUnavailableResponse();
        }
        
        return buildOllamaUnavailableResponse();
    }

    private String buildPrompt(String topic, String difficulty) {
        String[] focusAreas = {
                "debugging a real bug",
                "explaining a production trade-off",
                "designing a small feature",
                "optimizing existing code",
                "comparing two implementation choices",
                "handling an edge case"
        };
        String focusArea = focusAreas[ThreadLocalRandom.current().nextInt(focusAreas.length)];
        String[] interviewStyles = {
                "frontend engineer screen",
                "backend engineer screen",
                "product-company technical round",
                "startup coding discussion",
                "senior engineer concept check",
                "system troubleshooting round"
        };
        String interviewStyle = interviewStyles[ThreadLocalRandom.current().nextInt(interviewStyles.length)];

        return "Generate a single " + difficulty + " level interview question about " + topic + ".\n\n" +
               "Format your response EXACTLY like this:\n" +
               "QUESTION: [The interview question]\n" +
               "ANSWER: [A concise answer explanation]\n\n" +
               "Make the question practical and relevant to real interview questions candidates see online.\n" +
               "Use this fresh angle: " + focusArea + ".\n" +
               "Interview style: " + interviewStyle + ".\n" +
               "Do not repeat a standard textbook question or your previous response.\n" +
               "Variation id: " + UUID.randomUUID();
    }

    private Map<String, String> parseGeneratedQuestion(String generatedText) {
        Map<String, String> result = new HashMap<>();
        
        try {
            // Extract question
            int questionStart = generatedText.indexOf("QUESTION:");
            int answerStart = generatedText.indexOf("ANSWER:");
            
            if (questionStart != -1 && answerStart != -1) {
                String question = generatedText.substring(questionStart + 9, answerStart).trim();
                String answer = generatedText.substring(answerStart + 7).trim();
                
                result.put("question", question);
                result.put("answer", answer);
            } else {
                // Fallback: use the entire response as question
                result.put("question", generatedText.trim());
                result.put("answer", "See the question above for guidance.");
            }
        } catch (Exception e) {
            result.put("question", generatedText);
            result.put("answer", "Generated content.");
        }
        
        return result;
    }

    private Map<String, String> buildOllamaUnavailableResponse() {
        return Map.of(
                "question", "Ollama is not available right now.",
                "answer", "Start Ollama and make sure the selected model is installed, then click Generate AI Question again."
        );
    }

    public Set<String> getTopics() {
        return Set.of(
            "JavaScript", "React", "Java", "Python", "C++", "DSA",
            "Operating Systems", "Computer Networks", "Database",
            "Spring Boot", "Machine Learning", "System Design", "Web", "Backend"
        );
    }
}
