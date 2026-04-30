package com.interviewprep.backend.controller;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.backend.model.User;
import com.interviewprep.backend.model.EmailVerification;
import com.interviewprep.backend.repository.UserRepository;
import com.interviewprep.backend.repository.EmailVerificationRepository;
import com.interviewprep.backend.service.EmailService;
import com.interviewprep.backend.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, EmailService emailService, EmailVerificationRepository emailVerificationRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        String otp = request.get("otp");

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already exists"));
        }

        // Verify OTP
        Optional<EmailVerification> verificationOpt = emailVerificationRepository.findByEmail(email);
        if (verificationOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please send OTP first"));
        }

        EmailVerification verification = verificationOpt.get();
        if (!verification.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid OTP"));
        }

        if (System.currentTimeMillis() > verification.getExpiry()) {
            return ResponseEntity.badRequest().body(Map.of("message", "OTP expired"));
        }

        // Valid OTP -> Create User
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        // Cleanup OTP
        emailVerificationRepository.deleteByEmail(email);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/send-registration-otp")
    public ResponseEntity<?> sendRegistrationOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        Long expiry = System.currentTimeMillis() + 15 * 60 * 1000; // 15 mins

        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .orElse(new EmailVerification());
        
        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiry(expiry);

        emailVerificationRepository.save(verification);
        emailService.sendRegistrationOtpEmail(email, otp);

        return ResponseEntity.ok(Map.of("message", "OTP sent successfully to your email"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not found"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid password"));
        }

        String token = jwtUtil.generateToken(email);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "name", user.getName(),
                "email", user.getEmail()
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Generate 6-digit OTP
            String otp = String.format("%06d", new Random().nextInt(999999));
            // Set 15 minutes expiry
            user.setResetToken(otp);
            user.setResetTokenExpiry(System.currentTimeMillis() + 15 * 60 * 1000);
            userRepository.save(user);

            // Send actual email using EmailService
            emailService.sendPasswordResetEmail(user.getEmail(), otp);

            return ResponseEntity.ok(Map.of(
                "message", "If the email was found, a reset code was generated and sent!"
            ));
        }

        // Silent success for security in real apps
        return ResponseEntity.ok(Map.of("message", "If the email was found, a reset code was generated and sent!"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid reset code"));
        }

        User user = userOpt.get();

        if (user.getResetToken() == null || !user.getResetToken().equals(token) || user.getResetTokenExpiry() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid reset code"));
        }

        if (System.currentTimeMillis() > user.getResetTokenExpiry()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Reset code expired"));
        }

        // Valid OTP -> reset password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password reset successfully. You can now login."));
    }
}
