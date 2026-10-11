package com.coderound.backend.service;

import com.coderound.backend.client.Judge0Client;
import com.coderound.backend.client.Judge0SubmissionResponse;
import com.coderound.backend.dto.CodeRunRequest;
import com.coderound.backend.dto.CodeRunResponse;
import com.coderound.backend.dto.TestCaseResultItem;
import com.coderound.backend.entity.AssessmentSession;
import com.coderound.backend.entity.TestCase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeExecutionService {

    private final AssessmentSessionService sessionService;
    private final QuestionAttemptService attemptService;
    private final TestCaseService testCaseService;
    private final Judge0Client judge0Client;

    public CodeExecutionService(
            AssessmentSessionService sessionService,
            QuestionAttemptService attemptService,
            TestCaseService testCaseService,
            Judge0Client judge0Client
    ) {
        this.sessionService = sessionService;
        this.attemptService = attemptService;
        this.testCaseService = testCaseService;
        this.judge0Client = judge0Client;
    }

    public CodeRunResponse runSampleTests(
            String sessionCode,
            Long questionId,
            CodeRunRequest request
    ) {
        // 1. Verify active session & question assignment
        AssessmentSession session = sessionService.getActiveSessionByCode(sessionCode);
        sessionService.getAssignedQuestion(sessionCode, questionId);

        // 2. Save current code as attempt
        attemptService.saveAttempt(
                sessionCode,
                questionId,
                request.getLanguage(),
                request.getSourceCode()
        );

        // 3. Fetch visible test cases (never hidden for Run Code)
        List<TestCase> sampleCases = testCaseService.getVisibleTestCases(questionId);

        List<TestCaseResultItem> results = new ArrayList<>();
        int passedCount = 0;
        double totalTime = 0.0;
        int peakMemory = 0;
        String overallStatus = "ACCEPTED";

        if (sampleCases.isEmpty()) {
            // If no test cases configured yet, run once with dummy/empty input
            TestCaseResultItem item = executeSingleCase(
                    null,
                    "",
                    "",
                    request.getSourceCode(),
                    request.getLanguage()
            );
            results.add(item);
            if (item.isPassed()) {
                passedCount++;
            } else {
                overallStatus = item.getStatus();
            }
            totalTime += item.getExecutionTime() != null ? item.getExecutionTime() : 0.0;
            peakMemory = Math.max(peakMemory, item.getMemoryUsed() != null ? item.getMemoryUsed() : 0);

        } else {
            for (TestCase tc : sampleCases) {
                TestCaseResultItem item = executeSingleCase(
                        tc.getId(),
                        tc.getInput(),
                        tc.getExpectedOutput(),
                        request.getSourceCode(),
                        request.getLanguage()
                );

                results.add(item);

                if (item.isPassed()) {
                    passedCount++;
                } else if ("ACCEPTED".equals(overallStatus)) {
                    overallStatus = item.getStatus();
                }

                if (item.getExecutionTime() != null) {
                    totalTime += item.getExecutionTime();
                }
                if (item.getMemoryUsed() != null) {
                    peakMemory = Math.max(peakMemory, item.getMemoryUsed());
                }

                // If compilation error, short circuit remaining sample cases
                if ("COMPILATION_ERROR".equals(item.getStatus())) {
                    overallStatus = "COMPILATION_ERROR";
                    break;
                }
            }
        }

        if (passedCount < results.size() && "ACCEPTED".equals(overallStatus)) {
            overallStatus = "WRONG_ANSWER";
        }

        return new CodeRunResponse(
                overallStatus,
                results.size(),
                passedCount,
                Math.round(totalTime * 100.0) / 100.0,
                peakMemory,
                results
        );
    }

    private TestCaseResultItem executeSingleCase(
            Long testCaseId,
            String input,
            String expectedOutput,
            String sourceCode,
            String language
    ) {
        Judge0SubmissionResponse jResponse = judge0Client.execute(
                sourceCode,
                language,
                input != null ? input : "",
                expectedOutput != null ? expectedOutput : ""
        );

        String actualOutput = jResponse.getStdout() != null ? jResponse.getStdout() : "";
        String errorOutput = jResponse.getStderr();
        String compileOutput = jResponse.getCompileOutput();

        Double time = parseTime(jResponse.getTime());
        Integer memory = jResponse.getMemory();

        String statusStr = mapJudge0Status(jResponse);
        boolean passed = false;

        if ("ACCEPTED".equals(statusStr)) {
            passed = normalizeOutput(actualOutput).equals(normalizeOutput(expectedOutput));
            if (!passed) {
                statusStr = "WRONG_ANSWER";
            }
        }

        return new TestCaseResultItem(
                testCaseId,
                input,
                expectedOutput,
                actualOutput,
                passed,
                statusStr,
                compileOutput,
                errorOutput,
                time,
                memory
        );
    }

    private String mapJudge0Status(Judge0SubmissionResponse response) {
        if (response.getStatus() == null) {
            return "INTERNAL_ERROR";
        }

        int id = response.getStatus().getId();
        return switch (id) {
            case 3 -> "ACCEPTED";
            case 4 -> "WRONG_ANSWER";
            case 5 -> "TIME_LIMIT_EXCEEDED";
            case 6 -> "COMPILATION_ERROR";
            case 7, 8, 9, 10, 11, 12 -> "RUNTIME_ERROR";
            default -> "INTERNAL_ERROR";
        };
    }

    private String normalizeOutput(String output) {
        if (output == null) {
            return "";
        }
        return output.replace("\r\n", "\n")
                .trim();
    }

    private Double parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(timeStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
