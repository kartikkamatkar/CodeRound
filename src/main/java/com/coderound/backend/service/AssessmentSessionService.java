package com.coderound.backend.service;

import com.coderound.backend.entity.AssessmentQuestion;
import com.coderound.backend.entity.AssessmentSession;
import com.coderound.backend.enums.AssessmentStatus;
import com.coderound.backend.entity.Question;
import com.coderound.backend.exception.SessionNotFoundException;
import com.coderound.backend.repository.AssessmentQuestionRepository;
import com.coderound.backend.repository.AssessmentSessionRepository;
import com.coderound.backend.repository.QuestionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssessmentSessionService {

    private static final int REQUIRED_QUESTION_COUNT = 4;
    private static final long SESSION_DURATION_MINUTES = 60;

    private final AssessmentSessionRepository sessionRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final QuestionRepository questionRepository;

    public AssessmentSessionService(
            AssessmentSessionRepository sessionRepository,
            AssessmentQuestionRepository assessmentQuestionRepository,
            QuestionRepository questionRepository) {

        this.sessionRepository = sessionRepository;
        this.assessmentQuestionRepository = assessmentQuestionRepository;
        this.questionRepository = questionRepository;
    }

    public AssessmentSession createSession(List<Long> questionIds) {

        validateQuestionIds(questionIds);

        Set<Long> uniqueQuestionIds = new HashSet<>(questionIds);

        if (uniqueQuestionIds.size() != REQUIRED_QUESTION_COUNT) {
            throw new IllegalArgumentException(
                    "Duplicate question IDs are not allowed");
        }

        List<Question> questions =
                questionRepository.findAllById(uniqueQuestionIds);

        if (questions.size() != REQUIRED_QUESTION_COUNT) {
            throw new IllegalArgumentException(
                    "One or more questions do not exist");
        }

        var questionsById = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        OffsetDateTime now = OffsetDateTime.now();

        AssessmentSession session = new AssessmentSession();
        session.setSessionCode(generateUniqueSessionCode());
        session.setStatus(AssessmentStatus.IN_PROGRESS);
        session.setStartedAt(now);
        session.setExpiresAt(now.plusMinutes(SESSION_DURATION_MINUTES));

        AssessmentSession savedSession =
                sessionRepository.save(session);

        for (int i = 0; i < questionIds.size(); i++) {

            Long questionId = questionIds.get(i);
            Question question = questionsById.get(questionId);

            AssessmentQuestion assessmentQuestion =
                    new AssessmentQuestion();

            assessmentQuestion.setAssessmentSession(savedSession);
            assessmentQuestion.setQuestion(question);
            assessmentQuestion.setQuestionOrder(i + 1);

            assessmentQuestionRepository.save(assessmentQuestion);
        }

        return savedSession;
    }

    private void validateQuestionIds(List<Long> questionIds) {

        if (questionIds == null
                || questionIds.size() != REQUIRED_QUESTION_COUNT) {
            throw new IllegalArgumentException(
                    "Exactly four questions must be assigned");
        }

        if (questionIds.stream().anyMatch(
                id -> id == null || id <= 0)) {
            throw new IllegalArgumentException(
                    "Question IDs must be positive numbers");
        }
    }

    private String generateUniqueSessionCode() {

        String sessionCode;

        do {
            sessionCode = "CR-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase(Locale.ROOT);

        } while (sessionRepository.existsBySessionCode(sessionCode));

        return sessionCode;
    }

    @Transactional(readOnly = true)
    public AssessmentSession getSessionByCode(String sessionCode) {

        validateSessionCode(sessionCode);

        return sessionRepository.findBySessionCode(sessionCode.trim())
                .orElseThrow(() ->
                        new SessionNotFoundException(
                                "Session not found: " + sessionCode));
    }

    @Transactional
    public AssessmentSession getActiveSessionByCode(String sessionCode) {

        AssessmentSession session = getSessionByCode(sessionCode);

        if (session.getStatus() != AssessmentStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Session is not active");
        }

        if (session.getExpiresAt() == null
                || !session.getExpiresAt().isAfter(OffsetDateTime.now())) {

            session.setStatus(AssessmentStatus.EXPIRED);

            sessionRepository.save(session);

            throw new IllegalStateException(
                    "Assessment session has expired");
        }

        return session;
    }

    private void validateSessionCode(String sessionCode) {

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Session code is required");
        }
    }
}