package com.interviewprep.backend.repository;

import com.interviewprep.backend.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByTopic(String topic);
    List<Question> findByDifficulty(String difficulty);
    List<Question> findByType(String type);
    List<Question> findByCompany(String company);
    List<Question> findByTopicAndDifficulty(String topic, String difficulty);
}