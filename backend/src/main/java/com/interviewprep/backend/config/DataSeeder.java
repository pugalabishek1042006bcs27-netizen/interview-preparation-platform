package com.interviewprep.backend.config;

import com.interviewprep.backend.model.Question;
import com.interviewprep.backend.repository.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    public DataSeeder(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) {
        if (questionRepository.count() == 0) {
            questionRepository.saveAll(List.of(
                q("What is an Array?", "An array stores elements at contiguous memory locations. Access is O(1), insertion is O(n).", "DSA", "Easy", "Technical", "TCS"),
                q("Explain Binary Search", "Binary search divides the search interval in half each step. Time complexity is O(log n).", "DSA", "Medium", "Technical", "Infosys"),
                q("What is a Linked List?", "A linked list stores elements in nodes connected by pointers. No random access, insertion is O(1).", "DSA", "Easy", "Technical", "Wipro"),
                q("Explain Stack and Queue", "Stack follows LIFO (Last In First Out). Queue follows FIFO (First In First Out).", "DSA", "Easy", "Technical", "TCS"),
                q("What is a Binary Tree?", "A binary tree is a hierarchical structure where each node has at most two children: left and right.", "DSA", "Medium", "Technical", "Google"),
                q("Explain Bubble Sort", "Bubble sort repeatedly compares and swaps adjacent elements. Time complexity is O(n^2).", "DSA", "Easy", "Technical", "Infosys"),
                q("What is Dynamic Programming?", "DP breaks problems into overlapping subproblems and stores results to avoid recomputation.", "DSA", "Hard", "Technical", "Amazon"),
                q("Explain HashMap", "HashMap stores key-value pairs using hashing. Average time complexity for get/put is O(1).", "DSA", "Medium", "Technical", "Microsoft"),
                q("What is Recursion?", "Recursion is when a function calls itself. Needs a base case to stop. Uses call stack memory.", "DSA", "Medium", "Technical", "Google"),
                q("Explain BFS and DFS", "BFS explores level by level using a queue. DFS explores depth-first using a stack or recursion.", "DSA", "Hard", "Technical", "Amazon"),
                q("Tell me about yourself", "Structure your answer: 1) Current role/education, 2) Past experience, 3) Future goals. Keep it 90 seconds.", "HR", "Easy", "HR", "TCS"),
                q("What are your strengths?", "Pick 2-3 job-relevant strengths. Back each with a specific example using STAR method.", "HR", "Easy", "HR", "Infosys"),
                q("What are your weaknesses?", "Name a real weakness. Show self-awareness and explain the concrete steps you are taking to improve.", "HR", "Easy", "HR", "Wipro"),
                q("Why do you want to join us?", "Research the company values, products, and culture. Align them with your own career goals.", "HR", "Medium", "HR", "Google"),
                q("Where do you see yourself in 5 years?", "Show growth ambition within the company. Avoid saying you want their job or plan to leave.", "HR", "Medium", "HR", "Microsoft"),
                q("Explain OOP concepts", "OOP has 4 pillars: Encapsulation (data hiding), Inheritance (reuse), Polymorphism (many forms), Abstraction (hide complexity).", "Java", "Medium", "Technical", "TCS"),
                q("What is JDK vs JRE vs JVM?", "JVM executes bytecode. JRE = JVM + libraries. JDK = JRE + compiler + development tools.", "Java", "Easy", "Technical", "Infosys"),
                q("What is Spring Boot?", "Spring Boot auto-configures Spring apps, removes boilerplate, and provides embedded servers like Tomcat.", "Java", "Medium", "Technical", "Wipro"),
                q("Explain REST API", "REST uses HTTP methods: GET (read), POST (create), PUT (update), DELETE (remove). Stateless architecture.", "Java", "Medium", "Technical", "Amazon"),
                q("What is MongoDB?", "MongoDB is a NoSQL database storing data as BSON documents. Schema-flexible and horizontally scalable.", "Database", "Easy", "Technical", "Microsoft")
            ));
            System.out.println("✅ 20 questions seeded successfully!");
        } else {
            System.out.println("✅ Questions already exist, skipping seed.");
        }
    }

    private Question q(String title, String description, String topic,
                       String difficulty, String type, String company) {
        Question q = new Question();
        q.setTitle(title);
        q.setDescription(description);
        q.setTopic(topic);
        q.setDifficulty(difficulty);
        q.setType(type);
        q.setCompany(company);
        q.setAnswer(description);
        return q;
    }
}
