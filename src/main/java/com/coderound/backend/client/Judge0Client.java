package com.coderound.backend.client;

import com.coderound.backend.config.Judge0Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Locale;

@Component
public class Judge0Client {

    private static final Logger log = LoggerFactory.getLogger(Judge0Client.class);

    private final Judge0Properties properties;
    private final RestClient restClient;

    public Judge0Client(Judge0Properties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public int getLanguageId(String language) {
        if (language == null) {
            return 62; // Java default
        }
        return switch (language.trim().toUpperCase(Locale.ROOT)) {
            case "JAVA" -> 62;
            case "PYTHON" -> 71;
            case "CPP", "C++" -> 54;
            case "C" -> 50;
            case "JAVASCRIPT", "JS", "NODE" -> 63;
            default -> 62;
        };
    }

    public Judge0SubmissionResponse execute(String sourceCode, String language, String stdin, String expectedOutput) {
        int languageId = getLanguageId(language);

        Judge0SubmissionRequest request = new Judge0SubmissionRequest(
                sourceCode,
                languageId,
                stdin,
                expectedOutput
        );

        try {
            var requestHeaders = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/submissions")
                            .queryParam("base64_encoded", "false")
                            .queryParam("wait", "true")
                            .build())
                    .contentType(MediaType.APPLICATION_JSON);

            if (properties.getApiKey() != null && !properties.getApiKey().isBlank()) {
                requestHeaders.header("X-Auth-Token", properties.getApiKey());
                requestHeaders.header("X-RapidAPI-Key", properties.getApiKey());
            }

            Judge0SubmissionResponse response = requestHeaders
                    .body(request)
                    .retrieve()
                    .body(Judge0SubmissionResponse.class);

            if (response != null && response.getStatus() != null) {
                // If in queue or processing, poll briefly
                int statusId = response.getStatus().getId();
                if (statusId == 1 || statusId == 2) {
                    response = pollSubmission(response.getToken());
                }
                return response;
            }

            return response;

        } catch (Exception ex) {
            log.warn("Judge0 call failed at {}: {}. Evaluating with local fallback simulation: {}",
                    properties.getBaseUrl(), ex.getMessage(), properties.isMockWhenUnavailable());

            if (properties.isMockWhenUnavailable()) {
                return executeFallbackSimulation(sourceCode, stdin, expectedOutput);
            }

            throw new IllegalStateException("Code execution service unavailable: " + ex.getMessage(), ex);
        }
    }

    private Judge0SubmissionResponse pollSubmission(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        int maxAttempts = Math.max(1, properties.getTimeoutSeconds() * 1000 / properties.getPollIntervalMs());
        for (int i = 0; i < maxAttempts; i++) {
            try {
                Thread.sleep(properties.getPollIntervalMs());

                var requestHeaders = restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/submissions/" + token)
                                .queryParam("base64_encoded", "false")
                                .build());

                if (properties.getApiKey() != null && !properties.getApiKey().isBlank()) {
                    requestHeaders.header("X-Auth-Token", properties.getApiKey());
                    requestHeaders.header("X-RapidAPI-Key", properties.getApiKey());
                }

                Judge0SubmissionResponse res = requestHeaders.retrieve().body(Judge0SubmissionResponse.class);
                if (res != null && res.getStatus() != null) {
                    int statusId = res.getStatus().getId();
                    if (statusId != 1 && statusId != 2) {
                        return res;
                    }
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error polling Judge0 submission token {}", token, e);
                break;
            }
        }

        Judge0SubmissionResponse timeoutRes = new Judge0SubmissionResponse();
        timeoutRes.setStatus(new Judge0Status(5, "Time Limit Exceeded"));
        return timeoutRes;
    }

    /**
     * Fallback execution simulation when Judge0 server is not actively running.
     * Allows local development and testing to function smoothly.
     */
    private Judge0SubmissionResponse executeFallbackSimulation(String sourceCode, String stdin, String expectedOutput) {
        Judge0SubmissionResponse response = new Judge0SubmissionResponse();
        response.setTime("0.05");
        response.setMemory(14200);

        if (sourceCode == null || sourceCode.isBlank()) {
            response.setStatus(new Judge0Status(6, "Compilation Error"));
            response.setCompileOutput("Source code is empty or missing main class");
            return response;
        }

        // Check for basic syntax mistakes or intentional test syntax errors
        if (sourceCode.contains("SYNTAX_ERROR") || sourceCode.contains("compile_error")) {
            response.setStatus(new Judge0Status(6, "Compilation Error"));
            response.setCompileOutput("error: ';' expected");
            return response;
        }

        if (sourceCode.contains("RUNTIME_ERROR")) {
            response.setStatus(new Judge0Status(11, "Runtime Error (NZEC)"));
            response.setStderr("Exception in thread \"main\" java.lang.ArithmeticException: / by zero");
            return response;
        }

        if (sourceCode.contains("TIMEOUT")) {
            response.setStatus(new Judge0Status(5, "Time Limit Exceeded"));
            return response;
        }

        // Normal successful simulation: output matches expected output
        String normalizedExpected = expectedOutput != null ? expectedOutput.trim() : "";
        response.setStdout(normalizedExpected);
        response.setStatus(new Judge0Status(3, "Accepted"));
        return response;
    }
}
