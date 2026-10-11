
package com.coderound.backend.service;

import com.coderound.backend.entity.Question;
import com.coderound.backend.entity.Submission;
import com.coderound.backend.entity.SubmissionResult;
import com.coderound.backend.enums.SubmissionResultStatus;
import com.coderound.backend.enums.SubmissionStatus;
import com.coderound.backend.exception.QuestionNotFoundException;
import com.coderound.backend.repository.AssessmentQuestionRepository;
import com.coderound.backend.repository.QuestionRepository;
import com.coderound.backend.repository.SubmissionRepository;
import com.coderound.backend.repository.SubmissionResultRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubmissionResultService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;

    public SubmissionResultService(
            SubmissionRepository submissionRepository,
            SubmissionResultRepository resultRepository,
            QuestionRepository questionRepository,
            AssessmentQuestionRepository assessmentQuestionRepository) {

        this.submissionRepository = submissionRepository;
        this.resultRepository = resultRepository;
        this.questionRepository = questionRepository;
        this.assessmentQuestionRepository = assessmentQuestionRepository;
    }

    public SubmissionResult saveResult(
            Long submissionId,
            Long questionId,
            String language,
            String sourceCode,
            int totalTestCases,
            int passedTestCases,
            SubmissionResultStatus status,
            Integer executionTimeMs,
            Integer memoryUsedKb) {

        // 1. Validate identifiers
        if (submissionId == null || submissionId <= 0
                || questionId == null || questionId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid submission or question ID");
        }

        // 2. Validate required fields
        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException(
                    "Programming language is required");
        }

        if (sourceCode == null) {
            throw new IllegalArgumentException(
                    "Source code is required");
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "Result status is required");
        }

        // 3. Validate test-case counts
        if (totalTestCases < 0
                || passedTestCases < 0
                || passedTestCases > totalTestCases) {
            throw new IllegalArgumentException(
                    "Invalid test-case counts");
        }

        // 4. Validate resource metrics
        if (executionTimeMs != null && executionTimeMs < 0) {
            throw new IllegalArgumentException(
                    "Execution time cannot be negative");
        }

        if (memoryUsedKb != null && memoryUsedKb < 0) {
            throw new IllegalArgumentException(
                    "Memory usage cannot be negative");
        }

        // 5. Find the submission
        Submission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Submission not found"));

        // Only process submissions that are being evaluated.
        if (submission.getStatus() != SubmissionStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Submission must be PROCESSING to save results");
        }

        // 6. Verify the question exists
        Question question = questionRepository
                .findById(questionId)
                .orElseThrow(() ->
                        new QuestionNotFoundException(
                                "Question not found"));

        // 7. Verify the question belongs to this assessment
        Long sessionId =
                submission.getAssessmentSession().getId();

        boolean assigned =
                assessmentQuestionRepository
                        .existsByAssessmentSession_IdAndQuestion_Id(
                                sessionId, questionId);

        if (!assigned) {
            throw new IllegalArgumentException(
                    "Question does not belong to this submission");
        }

        // 8. Update an existing result or create a new one
        SubmissionResult result = resultRepository
                .findBySubmission_IdAndQuestion_Id(
                        submissionId, questionId)
                .orElseGet(SubmissionResult::new);

        result.setSubmission(submission);
        result.setQuestion(question);
        result.setLanguage(language.trim().toUpperCase());
        result.setSourceCode(sourceCode);
        result.setTotalTestCases(totalTestCases);
        result.setPassedTestCases(passedTestCases);
        result.setStatus(status);
        result.setExecutionTimeMs(executionTimeMs);
        result.setMemoryUsedKb(memoryUsedKb);

        return resultRepository.save(result);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResult> getResults(
            Long submissionId) {

        if (submissionId == null || submissionId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid submission ID");
        }

        Submission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Submission not found"));

        if (submission.getStatus() != SubmissionStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Results are available after submission completion");
        }

        return resultRepository
                .findBySubmission_IdOrderByQuestion_IdAsc(
                        submissionId);
    }
}