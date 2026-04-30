package com.interviewprep.backend.controller;

import com.interviewprep.backend.service.LinkedInAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/linkedin")
@CrossOrigin(origins = "*")
public class LinkedInAnalyzerController {

    @Autowired
    private LinkedInAnalyzerService linkedInService;

    @PostMapping("/analyze-text")
    public ResponseEntity<?> analyzeProfileText(@RequestBody Map<String, String> request) {
        String profileText = request.get("profileText");
        if (profileText == null || profileText.isEmpty()) {
            return ResponseEntity.badRequest().body("Profile content is required");
        }
        return ResponseEntity.ok(linkedInService.analyzeProfile(profileText));
    }

    @PostMapping("/analyze-file")
    public ResponseEntity<?> analyzeProfileFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid PDF file");
        }
        try {
            System.out.println("Analyzing LinkedIn PDF: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)");
            return ResponseEntity.ok(linkedInService.analyzeProfileFromPdf(file));
        } catch (Exception e) {
            System.err.println("LinkedIn PDF Analysis Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error parsing PDF: " + e.getMessage());
        }
    }

    @PostMapping("/match-job")
    public ResponseEntity<?> matchJob(@RequestBody Map<String, String> request) {
        String profileText = request.get("profileText");
        String jobDescription = request.get("jobDescription");
        
        if (profileText == null || jobDescription == null) {
            return ResponseEntity.badRequest().body("Profile text and Job Description are required");
        }
        
        return ResponseEntity.ok(linkedInService.matchJob(profileText, jobDescription));
    }
}
