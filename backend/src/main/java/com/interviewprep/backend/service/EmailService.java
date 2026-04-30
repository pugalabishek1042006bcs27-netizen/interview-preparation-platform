package com.interviewprep.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Interview Prep Platform - Password Reset Request");
        message.setText("Hello,\n\nYou have requested to reset your password. Please use the following 6-digit secure code to complete the process:\n\n" 
            + otp + "\n\nThis code will expire in 15 minutes.\n\n"
            + "If you did not request this reset, please ignore this email.\n\n"
            + "Best regards,\nInterview Prep Platform Team");
        mailSender.send(message);
    }

    public void sendRegistrationOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Interview Prep Platform - Verify Your Email");
        message.setText("Hello,\n\nThank you for joining Interview Prep Platform! To complete your registration, please use the following 6-digit verification code:\n\n" 
            + otp + "\n\nThis code will expire in 15 minutes.\n\n"
            + "If you did not request this verification, please ignore this email.\n\n"
            + "Best regards,\nInterview Prep Platform Team");
        mailSender.send(message);
    }
}
