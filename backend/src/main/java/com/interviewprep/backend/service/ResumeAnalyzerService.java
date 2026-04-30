package com.interviewprep.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeAnalyzerService {

    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;

    public ResumeAnalyzerService(OllamaService ollamaService, ObjectMapper objectMapper) {
        this.ollamaService = ollamaService;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> analyze(MultipartFile resumeFile, String jobDescription) throws IOException {
        String resumeText = extractResumeText(resumeFile);
        String prompt = buildPrompt(resumeText, jobDescription);
        String response = ollamaService.generate(prompt).trim();
        String normalizedResponse = extractJsonCandidate(response);

        try {
            return objectMapper.readValue(normalizedResponse, new TypeReference<>() {});
        } catch (Exception ignored) {
            return buildFallbackAnalysis(response);
        }
    }

    private String buildPrompt(String resumeText, String jobDescription) {
        return """
                You are an ATS resume analyzer and hiring coach.
                Analyze the resume against the job description.
                Return ONLY valid JSON with this exact shape and nothing else:
                {
                  "atsScore": 0,
                  "scoreExplanation": "",
                  "summary": "",
                  "strengths": ["", ""],
                  "missingKeywords": ["", ""],
                  "improvements": ["", ""],
                  "rewrittenSummary": "",
                  "rawAnalysis": ""
                }

                Rules:
                - atsScore must be an integer from 0 to 100 representing the match percentage
                - scoreExplanation must be 1-2 sentences explaining exactly why the resume received this score
                - summary must be 2-3 short sentences
                - strengths, missingKeywords, and improvements must each be an array of simple, extremely short bullet points (3-6 words per point, no full sentences).
                - rewrittenSummary should be a highly impactful 3-4 line professional summary
                - rawAnalysis should be a very brief outline.
                - Important: Do not write long paragraphs for the arrays. Keep them to short keywords or small points.
                - Do not include markdown fences or any text outside JSON
                - If some information is weak, still return your best estimate in valid JSON

                Resume:
                """ + resumeText + """

                Job Description:
                """ + jobDescription;
    }

    private String extractResumeText(MultipartFile resumeFile) throws IOException {
        String filename = resumeFile.getOriginalFilename() == null ? "" : resumeFile.getOriginalFilename().toLowerCase();

        if (filename.endsWith(".pdf")) {
            try (PDDocument document = Loader.loadPDF(resumeFile.getBytes())) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document).trim();
            }
        }

        if (filename.endsWith(".txt")) {
            return new String(resumeFile.getBytes()).trim();
        }

        throw new IOException("Unsupported file format. Please upload a PDF or TXT resume.");
    }

    private String extractJsonCandidate(String response) {
        int firstBrace = response.indexOf('{');
        int lastBrace = response.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return response.substring(firstBrace, lastBrace + 1);
        }
        return response;
    }

    private Map<String, Object> buildFallbackAnalysis(String response) {
        return Map.of(
                "atsScore", extractScore(response),
                "scoreExplanation", extractSection(response, "Score Explanation"),
                "summary", firstNonEmpty(
                        extractSection(response, "Summary"),
                        "The AI returned a non-JSON response, but the parsed analysis below is still usable."
                ),
                "strengths", extractBullets(response, "Strengths"),
                "missingKeywords", extractBullets(response, "Missing Keywords"),
                "improvements", extractBullets(response, "Improvements"),
                "rewrittenSummary", extractSection(response, "Suggested Summary"),
                "rawAnalysis", response
        );
    }

    private int extractScore(String response) {
        Matcher matcher = Pattern.compile("(\\d{1,3})\\s*/\\s*100|(\\d{1,3})").matcher(response);
        while (matcher.find()) {
            String value = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            int score = Integer.parseInt(value);
            if (score >= 0 && score <= 100) {
                return score;
            }
        }
        return 65;
    }

    private List<String> extractBullets(String response, String heading) {
        String section = extractSection(response, heading);
        if (section.isBlank()) {
            return List.of();
        }

        List<String> items = new ArrayList<>();
        for (String line : section.split("\\R")) {
            String normalized = line.replaceFirst("^[-*\\d.)\\s]+", "").trim();
            if (!normalized.isBlank()) {
                items.add(normalized);
            }
        }
        return items;
    }

    private String extractSection(String response, String heading) {
        String pattern = "(?is)" + Pattern.quote(heading) + "\\s*:?\\s*(.*?)(?=\\n\\s*[A-Z][A-Za-z ]+\\s*:?\\s|$)";
        Matcher matcher = Pattern.compile(pattern).matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private String firstNonEmpty(String first, String fallback) {
        return first != null && !first.isBlank() ? first : fallback;
    }
}
