package com.interviewprep.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class QuestionGeneratorService {

    private static final String GROQ_URL =
        "https://api.groq.com/openai/v1/chat/completions";

    private final String apiKey;
    private final String modelName;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuestionGeneratorService(
        @Value("${groq.api.key:}") String apiKey,
        @Value("${groq.model:llama3-8b-8192}") String modelName,
        @Value("${groq.timeout.ms:30000}") int timeoutMs
    ) {
        this.apiKey = apiKey;
        this.modelName = modelName;

        SimpleClientHttpRequestFactory requestFactory =
            new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public Map<String, String> generate(String topic, String difficulty) {
        if (apiKey == null || apiKey.isBlank()) {
            return buildAIUnavailableResponse();
        }

        try {
            String prompt = buildPrompt(topic, difficulty);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put(
                "messages",
                List.of(Map.of("role", "user", "content", prompt))
            );
            requestBody.put("max_tokens", 300);
            requestBody.put("temperature", 0.9);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(
                requestBody,
                headers
            );
            ResponseEntity<String> response = restTemplate.postForEntity(
                GROQ_URL,
                request,
                String.class
            );

            if (response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode content = jsonNode
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content");
                if (!content.isMissingNode() && !content.asText().isBlank()) {
                    return parseGeneratedQuestion(content.asText());
                }
            }
        } catch (RestClientException e) {
            return buildAIUnavailableResponse();
        } catch (Exception e) {
            return buildAIUnavailableResponse();
        }

        return buildAIUnavailableResponse();
    }

    private String buildPrompt(String topic, String difficulty) {
        String[] focusAreas = {
            "debugging a real bug",
            "explaining a production trade-off",
            "designing a small feature",
            "optimizing existing code",
            "comparing two implementation choices",
            "handling an edge case",
        };
        String focusArea = focusAreas[
            ThreadLocalRandom.current().nextInt(focusAreas.length)
        ];
        String[] interviewStyles = {
            "frontend engineer screen",
            "backend engineer screen",
            "product-company technical round",
            "startup coding discussion",
            "senior engineer concept check",
            "system troubleshooting round",
        };
        String interviewStyle = interviewStyles[
            ThreadLocalRandom.current().nextInt(interviewStyles.length)
        ];

        return (
            "Generate a single " +
            difficulty +
            " level interview question about " +
            topic +
            ".\n\n" +
            "Format your response EXACTLY like this:\n" +
            "QUESTION: [The interview question]\n" +
            "ANSWER: [A concise 2-3 sentence answer explanation]\n\n" +
            "Make the question practical and relevant to real interview questions candidates see online.\n" +
            "Use this fresh angle: " +
            focusArea +
            ".\n" +
            "Interview style: " +
            interviewStyle +
            ".\n" +
            "Do not repeat a standard textbook question or your previous response.\n" +
            "Variation id: " +
            UUID.randomUUID()
        );
    }

    private Map<String, String> parseGeneratedQuestion(String generatedText) {
        Map<String, String> result = new HashMap<>();

        try {
            // Extract question
            int questionStart = generatedText.indexOf("QUESTION:");
            int answerStart = generatedText.indexOf("ANSWER:");

            if (questionStart != -1 && answerStart != -1) {
                String question = generatedText
                    .substring(questionStart + 9, answerStart)
                    .trim();
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

    private Map<String, String> buildAIUnavailableResponse() {
        return Map.of(
            "question",
            "AI service is not available right now.",
            "answer",
            "Please ensure the GROQ_API_KEY environment variable is set in your deployment settings."
        );
    }

    public Set<String> getTopics() {
        return Set.of(
            "JavaScript",
            "React",
            "Java",
            "Python",
            "C++",
            "DSA",
            "Operating Systems",
            "Computer Networks",
            "Database",
            "Spring Boot",
            "Machine Learning",
            "System Design",
            "Web",
            "Backend"
        );
    }
}
