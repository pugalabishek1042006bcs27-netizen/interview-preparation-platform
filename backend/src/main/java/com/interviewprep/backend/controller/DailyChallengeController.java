package com.interviewprep.backend.controller;

import com.interviewprep.backend.model.Question;
import com.interviewprep.backend.model.User;
import com.interviewprep.backend.repository.UserRepository;
import com.interviewprep.backend.service.DailyChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/daily-challenge")
public class DailyChallengeController {

    @Autowired
    private DailyChallengeService dailyChallengeService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getDailyChallenge() {
        Question question = dailyChallengeService.getDailyChallenge();
        if (question == null) {
            return ResponseEntity.notFound().build();
        }

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email).orElse(null);

        return ResponseEntity.ok(Map.of(
                "question", question,
                "userStreak", user != null ? user.getCurrentStreak() : 0,
                "longestStreak", user != null ? user.getLongestStreak() : 0,
                "isCompletedToday", user != null && user.getLastChallengeDate() != null && 
                                   user.getLastChallengeDate().equals(java.time.LocalDate.now())
        ));
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeDailyChallenge(@RequestBody Map<String, String> request) {
        String answer = request.get("answer");
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            User updatedUser = dailyChallengeService.completeDailyChallenge(user.getId(), answer);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Daily challenge completed!",
                    "currentStreak", updatedUser.getCurrentStreak(),
                    "longestStreak", updatedUser.getLongestStreak()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
