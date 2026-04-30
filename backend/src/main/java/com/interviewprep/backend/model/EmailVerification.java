package com.interviewprep.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "email_verifications")
public class EmailVerification {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String otp;

    private Long expiry;

    public EmailVerification() {
    }

    public EmailVerification(String email, String otp, Long expiry) {
        this.email = email;
        this.otp = otp;
        this.expiry = expiry;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }
}
