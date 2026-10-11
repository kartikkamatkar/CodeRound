package com.coderound.backend.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssessmentSessionResponse {

    private String sessionCode;
    private String status;
    private OffsetDateTime startedAt;
    private OffsetDateTime expiresAt;
    private Long remainingSeconds;
    private List<AssignedQuestionSummary> questions = new ArrayList<>();

    public AssessmentSessionResponse() {
    }

    public AssessmentSessionResponse(
            String sessionCode,
            String status,
            OffsetDateTime startedAt,
            OffsetDateTime expiresAt,
            Long remainingSeconds,
            List<AssignedQuestionSummary> questions
    ) {
        this.sessionCode = sessionCode;
        this.status = status;
        this.startedAt = startedAt;
        this.expiresAt = expiresAt;
        this.remainingSeconds = remainingSeconds;
        if (questions != null) {
            this.questions = questions;
        }
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(Long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public List<AssignedQuestionSummary> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AssignedQuestionSummary> questions) {
        this.questions = questions;
    }
}
