package com.interviewprep.backend.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.backend.model.EmailVerification;

@Repository
public interface EmailVerificationRepository extends MongoRepository<EmailVerification, String> {
    Optional<EmailVerification> findByEmail(String email);
    void deleteByEmail(String email);
}
