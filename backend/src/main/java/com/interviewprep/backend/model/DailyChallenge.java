package com.interviewprep.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "daily_challenges")
public class DailyChallenge {

    @Id
    private String id;

    @Indexed(unique = true)
    private LocalDate date;

    private String questionId;

    public DailyChallenge() {
    }

    public DailyChallenge(LocalDate date, String questionId) {
        this.date = date;
        this.questionId = questionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
