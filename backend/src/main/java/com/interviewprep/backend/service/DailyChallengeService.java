package com.interviewprep.backend.service;

import com.interviewprep.backend.model.DailyChallenge;
import com.interviewprep.backend.model.Question;
import com.interviewprep.backend.model.User;
import com.interviewprep.backend.repository.DailyChallengeRepository;
import com.interviewprep.backend.repository.QuestionRepository;
import com.interviewprep.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DailyChallengeService {

    @Autowired
    private DailyChallengeRepository dailyChallengeRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public Question getDailyChallenge() {
        LocalDate today = LocalDate.now();
        return dailyChallengeRepository.findByDate(today)
                .map(challenge -> questionRepository.findById(challenge.getQuestionId()).orElse(null))
                .orElseGet(() -> createDailyChallenge(today));
    }

    private Question createDailyChallenge(LocalDate date) {
        List<Question> allQuestions = questionRepository.findAll();
        if (allQuestions.isEmpty()) {
            return null;
        }

        // Pick a random question
        Random random = new Random();
        Question randomQuestion = allQuestions.get(random.nextInt(allQuestions.size()));

        DailyChallenge challenge = new DailyChallenge(date, randomQuestion.getId());
        dailyChallengeRepository.save(challenge);

        return randomQuestion;
    }

    public User completeDailyChallenge(String userId, String userAnswer) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDate today = LocalDate.now();

        if (user.getLastChallengeDate() != null && user.getLastChallengeDate().equals(today)) {
            // Already completed today
            return user;
        }

        Question question = getDailyChallenge();
        if (question == null || !evaluateAnswer(question, userAnswer)) {
            throw new RuntimeException("Answer is not correct or too short. Please try again with more detail.");
        }

        if (user.getLastChallengeDate() != null && user.getLastChallengeDate().equals(today.minusDays(1))) {
            // Completed yesterday, increment streak
            user.setCurrentStreak(user.getCurrentStreak() + 1);
        } else {
            // Missed a day or first time, reset streak to 1
            user.setCurrentStreak(1);
        }

        if (user.getCurrentStreak() > user.getLongestStreak()) {
            user.setLongestStreak(user.getCurrentStreak());
        }

        user.setLastChallengeDate(today);
        return userRepository.save(user);
    }

    private boolean evaluateAnswer(Question question, String userAnswer) {
        if (userAnswer == null || userAnswer.trim().length() < 20) {
            return false;
        }

        String sampleAnswer = question.getAnswer().toLowerCase();
        String userAnsLower = userAnswer.toLowerCase();

        // Simple keyword matching: find meaningful words in sample answer
        Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "but", "is", "are", "was", "were", "to", "of", "in", "with", "it", "that", "this");
        List<String> keywords = Arrays.stream(sampleAnswer.split("\\W+"))
                .filter(word -> word.length() > 3 && !stopWords.contains(word))
                .distinct()
                .collect(Collectors.toList());

        if (keywords.isEmpty()) return true; // Fallback if no keywords extracted

        long matchCount = keywords.stream()
                .filter(userAnsLower::contains)
                .count();

        // Require at least 25% of keywords to match or at least 2 keywords
        return matchCount >= Math.min(2, Math.ceil(keywords.size() * 0.25));
    }
}
