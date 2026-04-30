package com.interviewprep.backend.controller;

import com.interviewprep.backend.service.OllamaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final OllamaService ollamaService;

    public InterviewController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/start")
    public Map<String, String> startInterview(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String prompt = "You are a technical interviewer. Ask me one clear " + topic +
                " interview question. Just ask the question directly, no intro needed.";
        String question = ollamaService.generate(prompt);
        String answerPrompt = "You are an expert interview coach. Give a concise, high-quality sample answer " +
                "for this " + topic + " interview question:\n\"" + question + "\"";
        String answer = ollamaService.generate(answerPrompt);
        return Map.of("question", question, "answer", answer);
    }

    @PostMapping("/answer")
    public Map<String, String> evaluateAnswer(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = request.get("answer");
        String prompt = "You are a technical interviewer. The interview question was: \"" + question +
                "\"\nThe candidate answered: \"" + answer +
                "\"\nEvaluate this answer. Give: 1) A score out of 10, 2) What was good, 3) What was missing, 4) A model answer. Be concise.";
        String feedback = ollamaService.generate(prompt);
        return Map.of("feedback", feedback);
    }
}
