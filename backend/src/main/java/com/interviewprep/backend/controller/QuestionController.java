package com.interviewprep.backend.controller;

import com.interviewprep.backend.model.Question;
import com.interviewprep.backend.repository.QuestionRepository;
import com.interviewprep.backend.service.QuestionGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final QuestionGeneratorService questionGeneratorService;

    public QuestionController(QuestionRepository questionRepository, QuestionGeneratorService questionGeneratorService) {
        this.questionRepository = questionRepository;
        this.questionGeneratorService = questionGeneratorService;
    }

    @GetMapping
    public List<Question> getQuestions(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String company) {

        if (topic != null && difficulty != null) {
            return questionRepository.findByTopicAndDifficulty(topic, difficulty);
        } else if (topic != null) {
            return questionRepository.findByTopic(topic);
        } else if (difficulty != null) {
            return questionRepository.findByDifficulty(difficulty);
        } else if (company != null) {
            return questionRepository.findByCompany(company);
        }
        return questionRepository.findAll();
    }

    @PostMapping
    public Question addQuestion(@RequestBody Question question) {
        return questionRepository.save(question);
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateQuestion(@RequestBody Map<String, String> request) {
        String topic = request.getOrDefault("topic", "JavaScript");
        String difficulty = request.getOrDefault("difficulty", "Medium");
        Map<String, String> result = questionGeneratorService.generate(topic, difficulty);
        return ResponseEntity.ok(result);
    }
}