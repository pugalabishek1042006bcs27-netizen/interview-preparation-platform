package com.interviewprep.backend.repository;

import com.interviewprep.backend.model.DailyChallenge;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyChallengeRepository extends MongoRepository<DailyChallenge, String> {
    Optional<DailyChallenge> findByDate(LocalDate date);
}
