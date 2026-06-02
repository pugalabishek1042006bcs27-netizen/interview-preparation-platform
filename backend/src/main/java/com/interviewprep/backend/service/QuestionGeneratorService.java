package com.interviewprep.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

@Service
public class QuestionGeneratorService {

    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "mistral"; // Change to "neural-chat", "llama2", etc if needed
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> generate(String topic, String difficulty) {
        try {
            String prompt = buildPrompt(topic, difficulty);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL_NAME);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);

            // Call Ollama API
            String response = restTemplate.postForObject(OLLAMA_API_URL, requestBody, String.class);
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                String generatedText = jsonNode.get("response").asText();
                
                // Parse the generated response to extract question and answer
                Map<String, String> result = parseGeneratedQuestion(generatedText);
                return result;
            }
        } catch (RestClientException e) {
            return Map.of(
                "question", "Ollama service is not running.",
                "answer", "Please make sure Ollama is running on http://localhost:11434. Install Ollama and run: ollama serve"
            );
        } catch (Exception e) {
            return Map.of(
                "question", "Error generating question: " + e.getMessage(),
                "answer", "Please try again."
            );
        }
        
        return Map.of(
            "question", "Unable to generate question.",
            "answer", "Please try again."
        );
    }

    private String buildPrompt(String topic, String difficulty) {
        return "Generate a single " + difficulty + " level interview question about " + topic + ".\n\n" +
               "Format your response EXACTLY like this:\n" +
               "QUESTION: [The interview question]\n" +
               "ANSWER: [A concise answer explanation]\n\n" +
               "Make the question practical and relevant to a real interview.";
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

    public Set<String> getTopics() {
        return Set.of(
            "JavaScript", "React", "Java", "Python", "C++", "DSA",
            "Operating Systems", "Computer Networks", "Database",
            "Spring Boot", "Machine Learning", "System Design", "Web", "Backend"
        );
    }
}
