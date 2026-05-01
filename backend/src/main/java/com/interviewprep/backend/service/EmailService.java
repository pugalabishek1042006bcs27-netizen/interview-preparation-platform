package com.interviewprep.backend.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    private final RestTemplate restTemplate;

    @Value("${google.script.url:}")
    private String googleScriptUrl;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private void sendViaGoogleScript(String to, String subject, String body) {
        if (googleScriptUrl == null || googleScriptUrl.isEmpty()) {
            System.err.println("Google Script URL is missing! Cannot send email to: " + to);
            System.err.println("Email Subject: " + subject);
            System.err.println("Email Body: \n" + body);
            return;
        }

        try {
            Map<String, String> payload = Map.of(
                "to", to,
                "subject", subject,
                "body", body
            );
            restTemplate.postForObject(googleScriptUrl, payload, String.class);
            System.out.println("Email successfully requested via Google Apps Script to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email via Google Apps Script: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String otp) {
        String subject = "Interview Prep Platform - Password Reset Request";
        String text = "Hello,\n\nYou have requested to reset your password. Please use the following 6-digit secure code to complete the process:\n\n" 
            + otp + "\n\nThis code will expire in 15 minutes.\n\n"
            + "If you did not request this reset, please ignore this email.\n\n"
            + "Best regards,\nInterview Prep Platform Team";
            
        sendViaGoogleScript(to, subject, text);
    }

    public void sendRegistrationOtpEmail(String to, String otp) {
        String subject = "Interview Prep Platform - Verify Your Email";
        String text = "Hello,\n\nThank you for joining Interview Prep Platform! To complete your registration, please use the following 6-digit verification code:\n\n" 
            + otp + "\n\nThis code will expire in 15 minutes.\n\n"
            + "If you did not request this verification, please ignore this email.\n\n"
            + "Best regards,\nInterview Prep Platform Team";
            
        sendViaGoogleScript(to, subject, text);
    }
}
