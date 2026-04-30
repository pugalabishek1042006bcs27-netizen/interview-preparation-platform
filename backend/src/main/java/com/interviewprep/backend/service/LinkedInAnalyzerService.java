package com.interviewprep.backend.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
public class LinkedInAnalyzerService {

    public Map<String, Object> analyzeProfileFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            if (text == null || text.trim().isEmpty()) {
                throw new IOException("The PDF was read but no text content was found. Ensure it's a direct export from LinkedIn.");
            }
            
            return analyzeProfile(text);
        }
    }

    public Map<String, Object> analyzeProfile(String profileText) {
        Map<String, Object> result = new HashMap<>();
        
        int score = calculateScore(profileText);
        String lowerText = profileText.toLowerCase();

        result.put("score", score);
        result.put("headlineStrength", score > 70 ? "Impactful & SEO Friendly" : "Generic - Needs keywords");
        
        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();
        List<String> missingKeywords = new ArrayList<>();

        // Section checks
        if (lowerText.contains("about") || lowerText.contains("summary")) strengths.add("Strong 'About' section presence");
        else improvements.add("Add a compelling 'About' section to tell your story");

        if (lowerText.contains("experience")) strengths.add("Structured work history");
        if (lowerText.contains("education")) strengths.add("Education credentials verified");
        if (lowerText.contains("recommendations")) strengths.add("Social proof via recommendations detected");
        else improvements.add("Request 2-3 recommendations to build credibility");

        // Keywords
        if (!lowerText.contains("agile") && !lowerText.contains("scrum")) missingKeywords.add("Project Management (Agile/Scrum)");
        if (!lowerText.contains("cloud") && !lowerText.contains("aws") && !lowerText.contains("azure")) missingKeywords.add("Cloud Infrastructure");
        if (!lowerText.contains("docker") && !lowerText.contains("kubernetes")) missingKeywords.add("DevOps (Docker/K8s)");

        result.put("strengths", strengths);
        result.put("improvements", improvements);
        result.put("missingKeywords", missingKeywords);
        
        // Dynamic Suggestions based on keywords
        result.put("headlines", generateHeadlines(profileText));
        result.put("summarySuggestion", "Passionate developer focused on building scalable web applications. Experienced in modern tech stacks and committed to continuous learning in Agile environments...");

        return result;
    }

    private List<String> generateHeadlines(String text) {
        String lower = text.toLowerCase();
        List<String> headlines = new ArrayList<>();
        
        String role = "Software Engineer";
        if (lower.contains("student")) role = "Aspiring Software Engineer";
        if (lower.contains("java")) role += " | Java Specialist";
        if (lower.contains("react")) role += " | Frontend Developer";

        headlines.add(role + " | Building Scalable Solutions");
        headlines.add("Tech Enthusiast & " + (lower.contains("student") ? "Computer Science Student" : "Software Professional") + " | Agile & Cloud Learner");
        headlines.add("Passionate about Problem Solving | " + (lower.contains("java") ? "Java & " : "") + "Full Stack Development");
        
        return headlines;
    }

    private int calculateScore(String text) {
        if (text == null || text.isEmpty()) return 0;
        int base = 50;
        if (text.length() > 2000) base += 15;
        if (text.toLowerCase().contains("experience")) base += 10;
        if (text.toLowerCase().contains("recommendations")) base += 10;
        if (text.toLowerCase().contains("projects")) base += 10;
        return Math.min(98, base);
    }

    public Map<String, Object> matchJob(String profileText, String jobDescription) {
        Map<String, Object> result = new HashMap<>();
        String[] keywords = {"java", "react", "spring", "sql", "aws", "docker", "agile", "python", "javascript"};
        
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        
        String pLower = profileText.toLowerCase();
        String jLower = jobDescription.toLowerCase();
        
        for (String kw : keywords) {
            if (jLower.contains(kw)) {
                if (pLower.contains(kw)) matched.add(kw.toUpperCase());
                else missing.add(kw.toUpperCase());
            }
        }
        
        int matchScore = matched.size() > 0 ? (int)((double)matched.size() / (matched.size() + missing.size()) * 100) : 0;
        
        result.put("matchScore", matchScore);
        result.put("matchedKeywords", matched);
        result.put("missingKeywords", missing);
        result.put("advice", matchScore > 70 ? "Excellent match! Apply now." : "Consider adding missing skills to your profile before applying.");
        
        return result;
    }
}
