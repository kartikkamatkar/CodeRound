package com.coderound.backend.service;

import com.coderound.backend.entity.AssessmentSession;
import com.coderound.backend.enums.AssessmentStatus;
import com.coderound.backend.entity.Submission;
import com.coderound.backend.enums.SubmissionStatus;
import com.coderound.backend.exception.SessionNotFoundException;
import com.coderound.backend.repository.AssessmentSessionRepository;
import com.coderound.backend.repository.SubmissionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Locale;

@Service
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssessmentSessionRepository sessionRepository;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            AssessmentSessionRepository sessionRepository) {

        this.submissionRepository = submissionRepository;
        this.sessionRepository = sessionRepository;
    }

    public Submission submitAssessment(String sessionCode) {

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Session code is required");
        }

        String normalizedSessionCode =
                sessionCode.trim().toUpperCase(Locale.ROOT);

        AssessmentSession session =
                sessionRepository.findBySessionCode(normalizedSessionCode)
                        .orElseThrow(() ->
                                new SessionNotFoundException(
                                        "Assessment session not found: "
                                                + normalizedSessionCode));

        if (session.getStatus() != AssessmentStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Assessment is not eligible for submission");
        }

        OffsetDateTime now = OffsetDateTime.now();

        if (session.getExpiresAt() == null
                || !session.getExpiresAt().isAfter(now)) {

            session.setStatus(AssessmentStatus.EXPIRED);
            sessionRepository.save(session);

            throw new IllegalStateException(
                    "Assessment session has expired");
        }

        if (submissionRepository
                .existsByAssessmentSession_Id(session.getId())) {
            throw new IllegalStateException(
                    "Assessment has already been submitted");
        }

        Submission submission = new Submission();
        submission.setAssessmentSession(session);
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setSubmittedAt(now);
        submission.setCompletedAt(null);

        session.setStatus(AssessmentStatus.SUBMITTED);
        session.setSubmittedAt(now);

        sessionRepository.save(session);

        return submissionRepository.save(submission);
    }
}