package com.coderound.backend.service;

import com.coderound.backend.client.Judge0Client;
import com.coderound.backend.client.Judge0SubmissionResponse;
import com.coderound.backend.dto.SubmissionResponse;
import com.coderound.backend.dto.SubmissionResultResponse;
import com.coderound.backend.entity.*;
import com.coderound.backend.enums.AssessmentStatus;
import com.coderound.backend.enums.SubmissionResultStatus;
import com.coderound.backend.enums.SubmissionStatus;
import com.coderound.backend.exception.ResourceNotFoundException;
import com.coderound.backend.repository.AssessmentSessionRepository;
import com.coderound.backend.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubmissionEvaluationService {

    private final AssessmentSessionService sessionService;
    private final AssessmentSessionRepository sessionRepository;
    private final QuestionAttemptService attemptService;
    private final TestCaseService testCaseService;
    private final SubmissionRepository submissionRepository;
    private final SubmissionResultService submissionResultService;
    private final Judge0Client judge0Client;

    public SubmissionEvaluationService(
            AssessmentSessionService sessionService,
            AssessmentSessionRepository sessionRepository,
            QuestionAttemptService attemptService,
            TestCaseService testCaseService,
            SubmissionRepository submissionRepository,
            SubmissionResultService submissionResultService,
            Judge0Client judge0Client
    ) {
        this.sessionService = sessionService;
        this.sessionRepository = sessionRepository;
        this.attemptService = attemptService;
        this.testCaseService = testCaseService;
        this.submissionRepository = submissionRepository;
        this.submissionResultService = submissionResultService;
        this.judge0Client = judge0Client;
    }

    @Transactional
    public SubmissionResponse submitAndEvaluate(String sessionCode) {
        // 1. Validate session status and expiry
        AssessmentSession session = sessionService.getSessionByCode(sessionCode);

        if (session.getStatus() != AssessmentStatus.IN_PROGRESS) {
            throw new IllegalStateException("Assessment is not eligible for submission: status is " + session.getStatus());
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (session.getExpiresAt() == null || !session.getExpiresAt().isAfter(now)) {
            session.setStatus(AssessmentStatus.EXPIRED);
            sessionRepository.save(session);
            throw new IllegalStateException("Assessment session has expired");
        }

        if (submissionRepository.existsByAssessmentSession_Id(session.getId())) {
            throw new IllegalStateException("Assessment has already been submitted");
        }

        // 2. Create Submission in PROCESSING state
        Submission submission = new Submission();
        submission.setAssessmentSession(session);
        submission.setStatus(SubmissionStatus.PROCESSING);
        submission.setSubmittedAt(now);
        submission = submissionRepository.save(submission);

        session.setStatus(AssessmentStatus.SUBMITTED);
        session.setSubmittedAt(now);
        sessionRepository.save(session);

        // 3. Evaluate each assigned question (all 4 assigned questions)
        List<AssessmentQuestion> assignedQuestions = sessionService.getAssignedQuestions(sessionCode);

        for (AssessmentQuestion aq : assignedQuestions) {
            Question question = aq.getQuestion();
            Optional<QuestionAttempt> attemptOpt = attemptService.findAttemptOptional(sessionCode, question.getId());

            String language = attemptOpt.map(QuestionAttempt::getLanguage).orElse("JAVA");
            String sourceCode = attemptOpt.map(QuestionAttempt::getSourceCode).orElse("");

            List<TestCase> allTestCases = testCaseService.getAllTestCasesForEvaluation(question.getId());

            int totalCases = allTestCases.size();
            int passedCases = 0;
            long totalExecutionTimeMs = 0;
            long peakMemoryKb = 0;
            SubmissionResultStatus resultStatus = SubmissionResultStatus.ACCEPTED;

            if (sourceCode.isBlank()) {
                resultStatus = SubmissionResultStatus.WRONG_ANSWER;
                passedCases = 0;
            } else {
                for (TestCase tc : allTestCases) {
                    Judge0SubmissionResponse jResponse = judge0Client.execute(
                            sourceCode,
                            language,
                            tc.getInput(),
                            tc.getExpectedOutput()
                    );

                    SubmissionResultStatus caseStatus = mapJudge0StatusToEnum(jResponse);

                    if (caseStatus == SubmissionResultStatus.ACCEPTED) {
                        String actual = normalizeOutput(jResponse.getStdout());
                        String expected = normalizeOutput(tc.getExpectedOutput());
                        if (actual.equals(expected)) {
                            passedCases++;
                        } else {
                            caseStatus = SubmissionResultStatus.WRONG_ANSWER;
                        }
                    }

                    if (jResponse.getTime() != null && !jResponse.getTime().isBlank()) {
                        try {
                            double sec = Double.parseDouble(jResponse.getTime());
                            totalExecutionTimeMs += (long) (sec * 1000);
                        } catch (NumberFormatException ignored) {}
                    }

                    if (jResponse.getMemory() != null) {
                        peakMemoryKb = Math.max(peakMemoryKb, jResponse.getMemory());
                    }

                    if (caseStatus == SubmissionResultStatus.COMPILATION_ERROR) {
                        resultStatus = SubmissionResultStatus.COMPILATION_ERROR;
                        break;
                    } else if (caseStatus != SubmissionResultStatus.ACCEPTED && resultStatus == SubmissionResultStatus.ACCEPTED) {
                        resultStatus = caseStatus;
                    }
                }

                if (passedCases < totalCases && resultStatus == SubmissionResultStatus.ACCEPTED) {
                    resultStatus = SubmissionResultStatus.WRONG_ANSWER;
                }
            }

            // Save individual SubmissionResult
            submissionResultService.saveResult(
                    submission.getId(),
                    question.getId(),
                    language,
                    sourceCode,
                    totalCases,
                    passedCases,
                    resultStatus,
                    (int) totalExecutionTimeMs,
                    (int) peakMemoryKb
            );
        }

        // 4. Mark Submission as COMPLETED
        submission.setStatus(SubmissionStatus.COMPLETED);
        submission.setCompletedAt(OffsetDateTime.now());
        submission = submissionRepository.save(submission);

        return getSubmissionDetails(submission.getId());
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionDetails(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        List<SubmissionResult> results = submissionResultService.getResults(submissionId);

        int totalQuestions = results.size();
        int fullySolved = (int) results.stream()
                .filter(r -> r.getStatus() == SubmissionResultStatus.ACCEPTED)
                .count();

        List<SubmissionResultResponse> resultResponses = results.stream()
                .map(r -> new SubmissionResultResponse(
                        r.getId(),
                        r.getQuestion().getId(),
                        r.getQuestion().getTitle(),
                        r.getQuestion().getDifficulty(),
                        r.getLanguage(),
                        r.getTotalTestCases(),
                        r.getPassedTestCases(),
                        r.getStatus().name(),
                        r.getExecutionTimeMs(),
                        r.getMemoryUsedKb()
                ))
                .collect(Collectors.toList());

        return new SubmissionResponse(
                submission.getId(),
                submission.getAssessmentSession().getSessionCode(),
                submission.getStatus().name(),
                submission.getSubmittedAt(),
                submission.getCompletedAt(),
                totalQuestions,
                fullySolved,
                resultResponses
        );
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionBySessionCode(String sessionCode) {
        Submission submission = submissionRepository.findByAssessmentSession_SessionCode(sessionCode)
                .orElseThrow(() -> new ResourceNotFoundException("No submission found for session code: " + sessionCode));

        return getSubmissionDetails(submission.getId());
    }

    private SubmissionResultStatus mapJudge0StatusToEnum(Judge0SubmissionResponse response) {
        if (response == null || response.getStatus() == null) {
            return SubmissionResultStatus.INTERNAL_ERROR;
        }

        return switch (response.getStatus().getId()) {
            case 3 -> SubmissionResultStatus.ACCEPTED;
            case 4 -> SubmissionResultStatus.WRONG_ANSWER;
            case 5 -> SubmissionResultStatus.TIME_LIMIT_EXCEEDED;
            case 6 -> SubmissionResultStatus.COMPILATION_ERROR;
            case 7, 8, 9, 10, 11, 12 -> SubmissionResultStatus.RUNTIME_ERROR;
            default -> SubmissionResultStatus.INTERNAL_ERROR;
        };
    }

    private String normalizeOutput(String output) {
        if (output == null) {
            return "";
        }
        return output.replace("\r\n", "\n").trim();
    }
}
