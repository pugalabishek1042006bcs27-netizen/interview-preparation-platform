package com.interviewprep.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/code")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class CodeExecutionController {

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/execute")
    public ResponseEntity<?> executeCode(@RequestBody CodeExecutionRequest request) {
        try {
            System.out.println("=== Code Execution Request ===");
            System.out.println("Language: " + request.getLanguage());
            System.out.println("Code: " + request.getCode().substring(0, Math.min(50, request.getCode().length())));
            
            if (request.getCode() == null || request.getCode().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("output", "Code cannot be empty");
                error.put("status", "❌ Error");
                return ResponseEntity.badRequest().body(error);
            }

            // Prepare request for Wandbox
            Map<String, Object> wandboxRequest = new HashMap<>();
            wandboxRequest.put("code", request.getCode());
            wandboxRequest.put("compiler", getWandboxCompiler(request.getLanguage()));
            wandboxRequest.put("save-temporary", true);
            if (request.getStdin() != null && !request.getStdin().isEmpty()) {
                wandboxRequest.put("stdin", request.getStdin());
            }

            System.out.println("Compiler: " + getWandboxCompiler(request.getLanguage()));
            
            // Setup headers for JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(wandboxRequest, headers);

            // Call Wandbox API
            String wandboxUrl = "https://wandbox.org/api/compile.json";
            System.out.println("Calling Wandbox API: " + wandboxUrl);
            
            ResponseEntity<String> wandboxResponseString = restTemplate.postForEntity(wandboxUrl, entity, String.class);
            JsonNode response = objectMapper.readTree(wandboxResponseString.getBody());
            
            // For logging purposes we use the status code of the String response
            HttpStatus statusCode = (HttpStatus) wandboxResponseString.getStatusCode();

            System.out.println("Wandbox Response Status: " + statusCode);
            System.out.println("Wandbox Response: " + (response != null ? response.toString() : "null"));

            // Extract output
            Map<String, Object> result = new HashMap<>();
            
            if (response != null && response.has("status") && "0".equals(response.get("status").asText())) {
                String output = response.has("program_output") && response.get("program_output") != null ? 
                    response.get("program_output").asText() : 
                    "Code executed successfully (no output)";
                result.put("success", true);
                result.put("output", output);
                result.put("status", "✅ Success");
                System.out.println("Success: " + output);
            } else {
                String error = "";
                if (response != null) {
                    if (response.has("compiler_error") && !response.get("compiler_error").asText().isEmpty()) {
                        error = response.get("compiler_error").asText();
                    } else if (response.has("program_error") && !response.get("program_error").asText().isEmpty()) {
                        error = response.get("program_error").asText();
                    } else if (response.has("compiler_message") && !response.get("compiler_message").asText().isEmpty()) {
                        error = response.get("compiler_message").asText();
                    } else if (response.has("program_message") && !response.get("program_message").asText().isEmpty()) {
                        error = response.get("program_message").asText();
                    } else {
                        error = "Execution failed: " + response.toString();
                    }
                } else {
                    error = "No response from Wandbox API";
                }
                result.put("success", false);
                result.put("output", error);
                result.put("status", "❌ Error");
                System.out.println("Error: " + error);
            }

            return ResponseEntity.ok(result);
        } catch (HttpClientErrorException e) {
            System.out.println("HTTP Error: " + e.getStatusCode() + " - " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("output", "HTTP Error: " + e.getStatusCode() + " - " + e.getMessage());
            error.put("status", "❌ Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("output", "Error: " + e.getMessage());
            error.put("status", "❌ Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private String getWandboxCompiler(String language) {
        if (language == null) {
            return "cpython-3.12.7";
        }
        switch (language.toLowerCase()) {
            case "python": return "cpython-3.12.7";
            case "java": return "openjdk-jdk-21+35";
            case "javascript": return "nodejs-20.17.0";
            case "c++": return "gcc-13.2.0";
            default: return "cpython-3.12.7";
        }
    }

    public static class CodeExecutionRequest {
        private String code;
        private String language;
        private String stdin;

        public CodeExecutionRequest() {}

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getStdin() {
            return stdin;
        }

        public void setStdin(String stdin) {
            this.stdin = stdin;
        }
    }
}
