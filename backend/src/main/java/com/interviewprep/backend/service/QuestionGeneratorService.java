package com.interviewprep.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

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

            String response = restTemplate.postForObject(ollamaApiUrl, requestBody, String.class);
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode responseNode = jsonNode.get("response");
                if (responseNode != null && !responseNode.asText().isBlank()) {
                    return parseGeneratedQuestion(responseNode.asText());
                }
            }
        } catch (RestClientException e) {
            return buildFallbackQuestion(topic, difficulty);
        } catch (Exception e) {
            return buildFallbackQuestion(topic, difficulty);
        }
        
        return buildFallbackQuestion(topic, difficulty);
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

    private Map<String, String> buildFallbackQuestion(String topic, String difficulty) {
        String normalizedTopic = getTopics().contains(topic) ? topic : "JavaScript";
        String normalizedDifficulty = Set.of("Easy", "Medium", "Hard").contains(difficulty)
                ? difficulty
                : "Medium";

        Map<String, String> fallback = fallbackQuestions().getOrDefault(
                normalizedTopic + ":" + normalizedDifficulty,
                fallbackQuestions().getOrDefault(normalizedTopic + ":Medium", fallbackQuestions().get("JavaScript:Medium"))
        );

        return Map.of(
                "question", fallback.get("question"),
                "answer", fallback.get("answer")
        );
    }

    private Map<String, Map<String, String>> fallbackQuestions() {
        return Map.ofEntries(
                Map.entry("JavaScript:Easy", Map.of(
                        "question", "What is the difference between var, let, and const in JavaScript?",
                        "answer", "var is function-scoped and can be re-declared, while let and const are block-scoped. let can be reassigned, but const cannot be reassigned after initialization."
                )),
                Map.entry("JavaScript:Medium", Map.of(
                        "question", "Explain closures in JavaScript and describe one practical use case.",
                        "answer", "A closure lets an inner function keep access to variables from its outer scope after that outer function has finished. It is commonly used for callbacks, function factories, and private state."
                )),
                Map.entry("JavaScript:Hard", Map.of(
                        "question", "How does the JavaScript event loop coordinate microtasks and macrotasks?",
                        "answer", "The call stack runs synchronous code first. After a macrotask completes, queued microtasks such as promise callbacks run before rendering and before the next macrotask, which affects ordering and responsiveness."
                )),
                Map.entry("React:Medium", Map.of(
                        "question", "When would you use useMemo, useCallback, or React.memo in a React application?",
                        "answer", "Use them when referential stability or expensive recalculation affects rendering cost. They are most useful around expensive derived values, stable callback props, and memoized child components."
                )),
                Map.entry("Java:Medium", Map.of(
                        "question", "What is the difference between an interface and an abstract class in Java?",
                        "answer", "Interfaces define capabilities a class can implement, while abstract classes can share state and partial implementation. A class can implement multiple interfaces but extend only one class."
                )),
                Map.entry("Python:Medium", Map.of(
                        "question", "What are Python decorators, and how would you use one in a real project?",
                        "answer", "A decorator wraps a function to add behavior without changing the function body. Common uses include logging, authorization checks, caching, and timing."
                )),
                Map.entry("C++:Medium", Map.of(
                        "question", "Explain RAII in C++ and why it helps prevent resource leaks.",
                        "answer", "RAII binds resource ownership to object lifetime. Constructors acquire resources and destructors release them, so cleanup happens predictably when objects leave scope."
                )),
                Map.entry("DSA:Medium", Map.of(
                        "question", "How would you detect a cycle in a linked list?",
                        "answer", "Use Floyd's two-pointer algorithm. Move one pointer one step and another two steps; if they meet, a cycle exists. If the fast pointer reaches null, there is no cycle."
                )),
                Map.entry("Operating Systems:Medium", Map.of(
                        "question", "What is the difference between a process and a thread?",
                        "answer", "A process has its own memory space and resources, while threads share the process memory. Threads are lighter to create but need synchronization when sharing mutable data."
                )),
                Map.entry("Computer Networks:Medium", Map.of(
                        "question", "What happens during a TCP three-way handshake?",
                        "answer", "The client sends SYN, the server responds with SYN-ACK, and the client replies with ACK. This establishes sequence numbers and confirms both sides can send and receive."
                )),
                Map.entry("Database:Medium", Map.of(
                        "question", "What is database indexing, and what trade-offs does it introduce?",
                        "answer", "Indexes speed up reads by creating faster lookup structures, but they consume storage and can slow writes because index entries must be maintained."
                )),
                Map.entry("Spring Boot:Medium", Map.of(
                        "question", "How does dependency injection work in Spring Boot?",
                        "answer", "Spring creates and manages beans in an application context, then injects dependencies through constructors, fields, or setters. Constructor injection is usually preferred for required dependencies."
                )),
                Map.entry("Machine Learning:Medium", Map.of(
                        "question", "What is overfitting, and how can you reduce it?",
                        "answer", "Overfitting happens when a model learns training noise instead of general patterns. It can be reduced with more data, regularization, cross-validation, simpler models, and early stopping."
                )),
                Map.entry("System Design:Medium", Map.of(
                        "question", "How would you design a rate limiter for an API?",
                        "answer", "Choose a strategy such as token bucket or sliding window, store counters in a fast shared system like Redis, define limits per user or key, and return clear retry information."
                )),
                Map.entry("Web:Medium", Map.of(
                        "question", "What is the difference between client-side rendering and server-side rendering?",
                        "answer", "Client-side rendering builds the UI in the browser after JavaScript loads. Server-side rendering sends ready HTML from the server, often improving first load and SEO."
                )),
                Map.entry("Backend:Medium", Map.of(
                        "question", "What makes an API idempotent, and why does it matter?",
                        "answer", "An idempotent operation produces the same result when repeated with the same input. It matters for retries, network failures, and predictable distributed systems behavior."
                ))
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
