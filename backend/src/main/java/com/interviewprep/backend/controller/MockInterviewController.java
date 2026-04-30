package com.interviewprep.backend.controller;

import com.interviewprep.backend.service.QuestionGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/mock")
public class MockInterviewController {

    private final QuestionGeneratorService questionGeneratorService;

    public MockInterviewController(QuestionGeneratorService questionGeneratorService) {
        this.questionGeneratorService = questionGeneratorService;
    }

    // Maps frontend topic names → QuestionGeneratorService topic names
    private static final Map<String, String> TOPIC_MAP = Map.ofEntries(
        Map.entry("JavaScript", "JavaScript"),
        Map.entry("React", "React"),
        Map.entry("Java", "Java"),
        Map.entry("Python", "Python"),
        Map.entry("C++", "C++"),
        Map.entry("DSA", "DSA"),
        Map.entry("Data Structures", "DSA"),
        Map.entry("Operating Systems", "Operating Systems"),
        Map.entry("OS", "Operating Systems"),
        Map.entry("Computer Networks", "Computer Networks"),
        Map.entry("Networking", "Computer Networks"),
        Map.entry("Database", "Database"),
        Map.entry("SQL", "Database"),
        Map.entry("Spring Boot", "Spring Boot"),
        Map.entry("Machine Learning", "Machine Learning"),
        Map.entry("ML", "Machine Learning"),
        Map.entry("System Design", "System Design"),
        Map.entry("Web", "Web"),
        Map.entry("Backend", "Backend"),
        Map.entry("Node.js", "Backend"),
        Map.entry("Behavioral", "Behavioral")
    );

    // Topic → Default difficulty
    private static final Map<String, String> TOPIC_DIFFICULTY = Map.ofEntries(
        Map.entry("JavaScript", "Medium"),
        Map.entry("React", "Medium"),
        Map.entry("Java", "Medium"),
        Map.entry("Python", "Medium"),
        Map.entry("C++", "Hard"),
        Map.entry("DSA", "Hard"),
        Map.entry("Operating Systems", "Medium"),
        Map.entry("Computer Networks", "Medium"),
        Map.entry("Database", "Medium"),
        Map.entry("Spring Boot", "Medium"),
        Map.entry("Machine Learning", "Medium"),
        Map.entry("System Design", "Hard"),
        Map.entry("Web", "Medium"),
        Map.entry("Backend", "Medium"),
        Map.entry("Behavioral", "Easy")
    );

    private static final List<Map<String, String>> BEHAVIORAL_QUESTIONS = List.of(
        Map.of("question", "Tell me about a time you had to work under pressure and meet a tight deadline.",
               "answer", "Use the STAR method: Situation, Task, Action, Result. Highlight problem-solving and time management."),
        Map.of("question", "Describe a situation where you had a conflict with a teammate and how you resolved it.",
               "answer", "Focus on communication, empathy, and keeping the team goal as priority. Show professionalism."),
        Map.of("question", "Tell me about your greatest professional achievement and what you learned from it.",
               "answer", "Choose a measurable achievement. Describe the challenge, your role, the outcome, and the lesson."),
        Map.of("question", "Why do you want to work for this company?",
               "answer", "Research the company's culture, products, and mission. Align with your career goals and values."),
        Map.of("question", "How do you prioritize your work when you have multiple deadlines?",
               "answer", "Discuss prioritization frameworks (urgency vs importance), communication with stakeholders, and task breakdown.")
    );

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startInterview(@RequestBody Map<String, String> request) {
        String rawTopic = request.getOrDefault("topic", "JavaScript");
        String difficulty = request.getOrDefault("difficulty", null);

        if ("Behavioral".equals(rawTopic)) {
            Map<String, String> q = BEHAVIORAL_QUESTIONS.get(new Random().nextInt(BEHAVIORAL_QUESTIONS.size()));
            return ResponseEntity.ok(Map.of("question", q.get("question")));
        }

        String mappedTopic = TOPIC_MAP.getOrDefault(rawTopic, "JavaScript");
        String resolvedDifficulty = (difficulty != null && !difficulty.equals("All"))
            ? difficulty
            : TOPIC_DIFFICULTY.getOrDefault(mappedTopic, "Medium");

        Map<String, String> result = questionGeneratorService.generate(mappedTopic, resolvedDifficulty);
        return ResponseEntity.ok(Map.of("question", result.get("question")));
    }

    @PostMapping("/feedback")
    public ResponseEntity<Map<String, String>> getFeedback(@RequestBody Map<String, String> request) {
        String topic = request.getOrDefault("topic", "JavaScript");
        String userAnswer = request.getOrDefault("answer", "");

        String feedback = generateFeedback(topic, userAnswer);
        return ResponseEntity.ok(Map.of("feedback", feedback));
    }

    private String generateFeedback(String topic, String userAnswer) {
        int wordCount = userAnswer.trim().isEmpty() ? 0 : userAnswer.trim().split("\\s+").length;
        StringBuilder fb = new StringBuilder();

        if (wordCount < 20) {
            fb.append("⚠️ Your answer is too brief. Aim for at least 3-4 structured sentences in a real interview.\n\n");
        } else if (wordCount > 200) {
            fb.append("✅ Great depth! Be mindful of pacing — keep spoken answers under 2-3 minutes.\n\n");
        } else {
            fb.append("✅ Good length and detail in your response.\n\n");
        }

        boolean hasExample = userAnswer.toLowerCase().contains("example") ||
                             userAnswer.toLowerCase().contains("for instance") ||
                             userAnswer.toLowerCase().contains("e.g") ||
                             userAnswer.contains("```");

        if (hasExample) {
            fb.append("🌟 Great use of examples — this significantly strengthens your answer.\n\n");
        } else {
            fb.append("💡 Tip: Add a concrete example or code snippet to make your answer more credible.\n\n");
        }

        switch (topic) {
            case "JavaScript":
                fb.append("📌 For JS questions, mention edge cases, ES6+ features, browser compatibility, and the event loop where relevant.");
                break;
            case "React":
                fb.append("📌 Mention hooks (useMemo, useCallback), component lifecycle, and rendering performance where relevant.");
                break;
            case "Java":
                fb.append("📌 Discuss OOP principles, JVM internals, Collections, and threading when applicable.");
                break;
            case "Python":
                fb.append("📌 Mention Python idioms, the GIL, generators, and library choices like NumPy/FastAPI where relevant.");
                break;
            case "C++":
                fb.append("📌 Discuss memory management, smart pointers, move semantics, and STL complexity guarantees.");
                break;
            case "DSA":
            case "Data Structures":
                fb.append("📌 Always state time and space complexity (Big-O). Compare alternative approaches and trade-offs.");
                break;
            case "Operating Systems":
            case "OS":
                fb.append("📌 Reference scheduling algorithms, synchronization primitives, and memory management specifics.");
                break;
            case "Computer Networks":
            case "Networking":
                fb.append("📌 Reference the OSI/TCP-IP model layers, protocols, and security mechanisms where applicable.");
                break;
            case "Database":
            case "SQL":
                fb.append("📌 Discuss query optimization, indexing strategies, and ACID vs BASE trade-offs.");
                break;
            case "Spring Boot":
                fb.append("📌 Mention auto-configuration, Spring context, bean lifecycle, and security filter chain where relevant.");
                break;
            case "Machine Learning":
            case "ML":
                fb.append("📌 Discuss model evaluation metrics, bias-variance tradeoff, and practical deployment considerations.");
                break;
            case "System Design":
                fb.append("📌 Cover scalability, reliability, CAP theorem, caching, load balancing, and database choices.");
                break;
            case "Behavioral":
                fb.append("📌 Use the STAR method (Situation, Task, Action, Result) for maximum clarity and impact.");
                break;
            default:
                fb.append("📌 Structure your answer: define the concept → explain why it matters → give a real-world example.");
        }

        return fb.toString();
    }
}
