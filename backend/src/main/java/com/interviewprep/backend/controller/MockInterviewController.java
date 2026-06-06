package com.interviewprep.backend.controller;

import com.interviewprep.backend.model.User;
import com.interviewprep.backend.repository.UserRepository;
import com.interviewprep.backend.service.QuestionGeneratorService;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mock")
public class MockInterviewController {

    private final QuestionGeneratorService questionGeneratorService;

    @Autowired
    private UserRepository userRepository;

    public MockInterviewController(
        QuestionGeneratorService questionGeneratorService
    ) {
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

    private static final List<Map<String, String>> BEHAVIORAL_QUESTIONS =
        List.of(
            Map.of(
                "question",
                "Tell me about a time you had to work under pressure and meet a tight deadline.",
                "answer",
                "Use the STAR method: Situation, Task, Action, Result. Highlight problem-solving and time management."
            ),
            Map.of(
                "question",
                "Describe a situation where you had a conflict with a teammate and how you resolved it.",
                "answer",
                "Focus on communication, empathy, and keeping the team goal as priority. Show professionalism."
            ),
            Map.of(
                "question",
                "Tell me about your greatest professional achievement and what you learned from it.",
                "answer",
                "Choose a measurable achievement. Describe the challenge, your role, the outcome, and the lesson."
            ),
            Map.of(
                "question",
                "Why do you want to work for this company?",
                "answer",
                "Research the company's culture, products, and mission. Align with your career goals and values."
            ),
            Map.of(
                "question",
                "How do you prioritize your work when you have multiple deadlines?",
                "answer",
                "Discuss prioritization frameworks (urgency vs importance), communication with stakeholders, and task breakdown."
            )
        );

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startInterview(
        @RequestBody Map<String, String> request
    ) {
        String rawTopic = request.getOrDefault("topic", "JavaScript");
        String difficulty = request.getOrDefault("difficulty", null);

        if ("Behavioral".equals(rawTopic)) {
            Map<String, String> q = BEHAVIORAL_QUESTIONS.get(
                new Random().nextInt(BEHAVIORAL_QUESTIONS.size())
            );
            return ResponseEntity.ok(Map.of("question", q.get("question")));
        }

        String mappedTopic = TOPIC_MAP.getOrDefault(rawTopic, "JavaScript");
        String resolvedDifficulty = (difficulty != null &&
            !difficulty.equals("All"))
            ? difficulty
            : TOPIC_DIFFICULTY.getOrDefault(mappedTopic, "Medium");

        Map<String, String> result = questionGeneratorService.generate(
            mappedTopic,
            resolvedDifficulty
        );
        return ResponseEntity.ok(Map.of("question", result.get("question")));
    }

    @PostMapping("/feedback")
    public ResponseEntity<Map<String, String>> getFeedback(
        @RequestBody Map<String, String> request
    ) {
        String topic = request.getOrDefault("topic", "JavaScript");
        String userAnswer = request.getOrDefault("answer", "");

        String feedback = generateFeedback(topic, userAnswer);
        return ResponseEntity.ok(Map.of("feedback", feedback));
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeInterview(
        @RequestBody Map<String, String> request
    ) {
        String email = (String) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        String topic = request.getOrDefault("topic", "General");
        String answer = request.getOrDefault("answer", "");
        int score = calculateScore(answer);

        // Increment count
        user.setTotalMockInterviews(user.getTotalMockInterviews() + 1);
        int total = user.getTotalMockInterviews();

        // Rolling average score
        double newAvg =
            ((user.getAverageScore() * (total - 1)) + score) / total;
        user.setAverageScore(newAvg);

        // Recent activity (keep latest 5)
        user.setRecentActivity(
            updateRecentActivity(user.getRecentActivity(), topic, score)
        );

        // Performance history (keep latest 10)
        user.setPerformanceHistory(
            updatePerformanceHistory(user.getPerformanceHistory(), score)
        );

        // Skill radar
        user.setSkillAnalysis(
            updateSkillAnalysis(user.getSkillAnalysis(), topic, score)
        );

        userRepository.save(user);
        return ResponseEntity.ok(
            Map.of("score", score, "totalMockInterviews", total)
        );
    }

    private int calculateScore(String answer) {
        if (answer == null || answer.trim().isEmpty()) return 30;
        int wordCount = answer.trim().split("\\s+").length;
        int score = 40;
        if (wordCount >= 20) score = 65;
        if (wordCount >= 50) score = 72;
        if (wordCount >= 100) score = 80;
        boolean hasExample =
            answer.toLowerCase().contains("example") ||
            answer.toLowerCase().contains("for instance") ||
            answer.toLowerCase().contains("e.g") ||
            answer.contains("```");
        if (hasExample) score += 10;
        return Math.min(score, 100);
    }

    private List<Map<String, Object>> updateRecentActivity(
        List<Map<String, Object>> current,
        String topic,
        int score
    ) {
        if (current == null) current = new ArrayList<>();
        Map<String, Object> entry = new HashMap<>();
        entry.put("action", "Mock Interview – " + topic);
        entry.put("date", LocalDate.now().toString());
        entry.put(
            "color",
            score >= 75 ? "green" : score >= 55 ? "blue" : "red"
        );
        entry.put("score", score + "%");
        current.add(0, entry);
        return current.stream().limit(5).collect(Collectors.toList());
    }

    private List<Map<String, Object>> updatePerformanceHistory(
        List<Map<String, Object>> current,
        int score
    ) {
        if (current == null) current = new ArrayList<>();
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", LocalDate.now().toString());
        entry.put("avgScore", score);
        current.add(entry);
        return current
            .stream()
            .skip(Math.max(0, current.size() - 10))
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> updateSkillAnalysis(
        Map<String, Object> current,
        String topic,
        int score
    ) {
        if (current == null) current = new HashMap<>();
        List<Map<String, Object>> subjects = (List<
            Map<String, Object>
        >) current.get("subjects");
        if (subjects == null) {
            subjects = new ArrayList<>(
                Arrays.asList(
                    mkSubject("DSA", 0),
                    mkSubject("Java Core", 0),
                    mkSubject("System Design", 0),
                    mkSubject("Databases", 0),
                    mkSubject("HR / Soft Skills", 0),
                    mkSubject("DevOps", 0)
                )
            );
        }
        String target = mapTopicToSubject(topic);
        for (Map<String, Object> s : subjects) {
            if (target.equals(s.get("subject"))) {
                int old = ((Number) s.getOrDefault("A", 0)).intValue();
                // Weighted average: 70 % history + 30 % latest
                s.put("A", (int) (old * 0.7 + score * 0.3));
                break;
            }
        }
        current.put("subjects", subjects);
        return current;
    }

    private Map<String, Object> mkSubject(String name, int value) {
        Map<String, Object> s = new HashMap<>();
        s.put("subject", name);
        s.put("A", value);
        s.put("fullMark", 100);
        return s;
    }

    private String mapTopicToSubject(String topic) {
        switch (topic) {
            case "DSA":
            case "Data Structures":
                return "DSA";
            case "System Design":
                return "System Design";
            case "Database":
            case "SQL":
                return "Databases";
            case "Behavioral":
                return "HR / Soft Skills";
            case "Operating Systems":
            case "OS":
            case "Computer Networks":
            case "Networking":
                return "DevOps";
            default:
                return "Java Core";
        }
    }

    private String generateFeedback(String topic, String userAnswer) {
        int wordCount = userAnswer.trim().isEmpty()
            ? 0
            : userAnswer.trim().split("\\s+").length;
        StringBuilder fb = new StringBuilder();

        if (wordCount < 20) {
            fb.append(
                "⚠️ Your answer is too brief. Aim for at least 3-4 structured sentences in a real interview.\n\n"
            );
        } else if (wordCount > 200) {
            fb.append(
                "✅ Great depth! Be mindful of pacing — keep spoken answers under 2-3 minutes.\n\n"
            );
        } else {
            fb.append("✅ Good length and detail in your response.\n\n");
        }

        boolean hasExample =
            userAnswer.toLowerCase().contains("example") ||
            userAnswer.toLowerCase().contains("for instance") ||
            userAnswer.toLowerCase().contains("e.g") ||
            userAnswer.contains("```");

        if (hasExample) {
            fb.append(
                "🌟 Great use of examples — this significantly strengthens your answer.\n\n"
            );
        } else {
            fb.append(
                "💡 Tip: Add a concrete example or code snippet to make your answer more credible.\n\n"
            );
        }

        switch (topic) {
            case "JavaScript":
                fb.append(
                    "📌 For JS questions, mention edge cases, ES6+ features, browser compatibility, and the event loop where relevant."
                );
                break;
            case "React":
                fb.append(
                    "📌 Mention hooks (useMemo, useCallback), component lifecycle, and rendering performance where relevant."
                );
                break;
            case "Java":
                fb.append(
                    "📌 Discuss OOP principles, JVM internals, Collections, and threading when applicable."
                );
                break;
            case "Python":
                fb.append(
                    "📌 Mention Python idioms, the GIL, generators, and library choices like NumPy/FastAPI where relevant."
                );
                break;
            case "C++":
                fb.append(
                    "📌 Discuss memory management, smart pointers, move semantics, and STL complexity guarantees."
                );
                break;
            case "DSA":
            case "Data Structures":
                fb.append(
                    "📌 Always state time and space complexity (Big-O). Compare alternative approaches and trade-offs."
                );
                break;
            case "Operating Systems":
            case "OS":
                fb.append(
                    "📌 Reference scheduling algorithms, synchronization primitives, and memory management specifics."
                );
                break;
            case "Computer Networks":
            case "Networking":
                fb.append(
                    "📌 Reference the OSI/TCP-IP model layers, protocols, and security mechanisms where applicable."
                );
                break;
            case "Database":
            case "SQL":
                fb.append(
                    "📌 Discuss query optimization, indexing strategies, and ACID vs BASE trade-offs."
                );
                break;
            case "Spring Boot":
                fb.append(
                    "📌 Mention auto-configuration, Spring context, bean lifecycle, and security filter chain where relevant."
                );
                break;
            case "Machine Learning":
            case "ML":
                fb.append(
                    "📌 Discuss model evaluation metrics, bias-variance tradeoff, and practical deployment considerations."
                );
                break;
            case "System Design":
                fb.append(
                    "📌 Cover scalability, reliability, CAP theorem, caching, load balancing, and database choices."
                );
                break;
            case "Behavioral":
                fb.append(
                    "📌 Use the STAR method (Situation, Task, Action, Result) for maximum clarity and impact."
                );
                break;
            default:
                fb.append(
                    "📌 Structure your answer: define the concept → explain why it matters → give a real-world example."
                );
        }

        return fb.toString();
    }
}
