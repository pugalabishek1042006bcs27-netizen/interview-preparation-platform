package com.interviewprep.backend.service;

import com.interviewprep.backend.model.User;
import com.interviewprep.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Service
public class UserStatsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Record a completed mock interview and update user statistics
     */
    public User recordMockInterviewCompletion(String userId, int score) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        // Increment total mock interviews
        int currentCount = user.getTotalMockInterviews();
        user.setTotalMockInterviews(currentCount + 1);
        
        // Update average score
        double currentAverage = user.getAverageScore();
        double newAverage = (currentAverage * currentCount + score) / (currentCount + 1);
        user.setAverageScore(newAverage);
        
        // Add to recent activity
        List<Map<String, Object>> recentActivity = user.getRecentActivity();
        if (recentActivity == null) recentActivity = new ArrayList<>();
        Map<String, Object> activity = new HashMap<>();
        activity.put("date", LocalDate.now().toString());
        activity.put("action", "Mock Interview Completed");
        activity.put("score", score + "%");
        activity.put("color", score >= 70 ? "green" : (score >= 50 ? "blue" : "red"));
        recentActivity.add(0, activity);
        if (recentActivity.size() > 5) {
            recentActivity = recentActivity.subList(0, 5);
        }
        user.setRecentActivity(recentActivity);

        // Update performance history
        List<Map<String, Object>> performanceHistory = user.getPerformanceHistory();
        if (performanceHistory == null) performanceHistory = new ArrayList<>();
        String currentMonth = LocalDate.now().getMonth().toString().substring(0, 3);
        String monthName = currentMonth.substring(0, 1).toUpperCase() + currentMonth.substring(1).toLowerCase();
        boolean monthFound = false;
        for (Map<String, Object> entry : performanceHistory) {
            if (monthName.equals(entry.get("name"))) {
                int oldScore = entry.get("avgScore") instanceof Integer ? (Integer) entry.get("avgScore") : ((Number) entry.get("avgScore")).intValue();
                entry.put("avgScore", (oldScore + score) / 2);
                monthFound = true;
                break;
            }
        }
        if (!monthFound) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", monthName);
            entry.put("avgScore", score);
            performanceHistory.add(entry);
        }
        user.setPerformanceHistory(performanceHistory);
        
        return userRepository.save(user);
    }

    /**
     * Record a solved question and update user statistics
     */
    public User recordQuestionSolved(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        // Increment questions solved
        int currentCount = user.getQuestionsSolved();
        user.setQuestionsSolved(currentCount + 1);
        
        return userRepository.save(user);
    }

    /**
     * Record multiple questions solved at once
     */
    public User recordQuestionsSolved(String userId, int count) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        int currentCount = user.getQuestionsSolved();
        user.setQuestionsSolved(currentCount + count);
        
        return userRepository.save(user);
    }

    /**
     * Get user statistics
     */
    public User getUserStats(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
