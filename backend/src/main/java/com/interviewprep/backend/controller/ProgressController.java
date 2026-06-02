package com.interviewprep.backend.controller;

import com.interviewprep.backend.model.User;
import com.interviewprep.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Map<String, Object>> performanceHistory = user.getPerformanceHistory();
        if (performanceHistory == null) performanceHistory = new ArrayList<>();
        
        Map<String, Object> skillAnalysis = user.getSkillAnalysis();
        if (skillAnalysis == null) {
            skillAnalysis = generateEmptySkillAnalysis();
        }
        
        List<Map<String, Object>> recentActivity = user.getRecentActivity();
        if (recentActivity == null) recentActivity = new ArrayList<>();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMockInterviews", user.getTotalMockInterviews());
        stats.put("averageScore", user.getAverageScore());
        stats.put("questionsSolved", user.getQuestionsSolved());
        stats.put("currentStreak", user.getCurrentStreak());
        stats.put("longestStreak", user.getLongestStreak());
        stats.put("lastChallengeDate", user.getLastChallengeDate());
        stats.put("performanceHistory", performanceHistory);
        stats.put("skillAnalysis", skillAnalysis);
        stats.put("recentActivity", recentActivity);

        return ResponseEntity.ok(stats);
    }

    private Map<String, Object> generateEmptySkillAnalysis() {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("subjects", new ArrayList<>());
        return analysis;
    }
}
