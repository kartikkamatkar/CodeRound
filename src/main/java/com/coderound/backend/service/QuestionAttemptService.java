
package com.coderound.backend.service;

import com.coderound.backend.entity.AssessmentSession;
import com.coderound.backend.entity.Question;
import com.coderound.backend.entity.QuestionAttempt;
import com.coderound.backend.exception.QuestionNotFoundException;
import com.coderound.backend.exception.SessionNotFoundException;
import com.coderound.backend.repository.AssessmentQuestionRepository;
import com.coderound.backend.repository.AssessmentSessionRepository;
import com.coderound.backend.repository.QuestionAttemptRepository;
import com.coderound.backend.repository.QuestionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class QuestionAttemptService {

    private static final Set<String> SUPPORTED_LANGUAGES =
            Set.of("JAVA");

    private static final int MAX_SOURCE_CODE_LENGTH = 100_000;

    private final QuestionAttemptRepository attemptRepository;
    private final AssessmentSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;

    public QuestionAttemptService(
            QuestionAttemptRepository attemptRepository,
            AssessmentSessionRepository sessionRepository,
            QuestionRepository questionRepository,
            AssessmentQuestionRepository assessmentQuestionRepository) {

        this.attemptRepository = attemptRepository;
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.assessmentQuestionRepository = assessmentQuestionRepository;
    }

    public QuestionAttempt saveAttempt(
            String sessionCode,
            Long questionId,
            String language,
            String sourceCode) {

        validateSessionCode(sessionCode);
        validateQuestionId(questionId);
        validateLanguage(language);
        validateSourceCode(sourceCode);

        AssessmentSession session = findSession(sessionCode);

        if (!"IN_PROGRESS".equals(session.getStatus().name())) {
            throw new IllegalStateException("Session is not active");
        }

        if (session.getExpiresAt() == null
                || !session.getExpiresAt().isAfter(OffsetDateTime.now())) {
            throw new IllegalStateException("Assessment session has expired");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new QuestionNotFoundException(
                                "Question not found with ID: " + questionId));

        validateQuestionAssignment(session.getId(), questionId);

        QuestionAttempt attempt = attemptRepository
                .findByAssessmentSession_IdAndQuestion_Id(
                        session.getId(), questionId)
                .orElseGet(QuestionAttempt::new);

        attempt.setAssessmentSession(session);
        attempt.setQuestion(question);
        attempt.setLanguage(language.trim().toUpperCase(Locale.ROOT));
        attempt.setSourceCode(sourceCode);
        attempt.setUpdatedAt(OffsetDateTime.now());

        return attemptRepository.save(attempt);
    }

    @Transactional(readOnly = true)
    public QuestionAttempt getAttempt(
            String sessionCode,
            Long questionId) {

        validateSessionCode(sessionCode);
        validateQuestionId(questionId);

        AssessmentSession session = findSession(sessionCode);

        validateQuestionAssignment(session.getId(), questionId);

        return attemptRepository
                .findByAssessmentSession_IdAndQuestion_Id(
                        session.getId(), questionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No saved attempt exists for this question"));
    }

    private AssessmentSession findSession(String sessionCode) {
        return sessionRepository.findBySessionCode(sessionCode.trim())
                .orElseThrow(() ->
                        new SessionNotFoundException(
                                "Session not found: " + sessionCode));
    }

    private void validateQuestionAssignment(
            Long sessionId,
            Long questionId) {

        boolean assigned = assessmentQuestionRepository
                .existsByAssessmentSession_IdAndQuestion_Id(
                        sessionId, questionId);

        if (!assigned) {
            throw new IllegalArgumentException(
                    "Question does not belong to this assessment session");
        }
    }

    private void validateSessionCode(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Session code is required");
        }
    }

    private void validateQuestionId(Long questionId) {
        if (questionId == null || questionId <= 0) {
            throw new IllegalArgumentException(
                    "Question ID must be a positive number");
        }
    }

    private void validateLanguage(String language) {
        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException(
                    "Programming language is required");
        }

        String normalizedLanguage =
                language.trim().toUpperCase(Locale.ROOT);

        if (!SUPPORTED_LANGUAGES.contains(normalizedLanguage)) {
            throw new IllegalArgumentException(
                    "Unsupported programming language: " + language);
        }
    }

    private void validateSourceCode(String sourceCode) {
        if (sourceCode == null || sourceCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Source code is required");
        }

        if (sourceCode.length() > MAX_SOURCE_CODE_LENGTH) {
            throw new IllegalArgumentException(
                    "Source code exceeds the maximum allowed length");
        }
    }
}