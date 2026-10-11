package com.coderound.backend.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class SubmissionResponse {

    private Long id;
    private String sessionCode;
    private String status;
    private OffsetDateTime submittedAt;
    private OffsetDateTime completedAt;
    private int totalQuestions;
    private int fullySolvedQuestions;
    private List<SubmissionResultResponse> results = new ArrayList<>();

    public SubmissionResponse() {
    }

    public SubmissionResponse(
            Long id,
            String sessionCode,
            String status,
            OffsetDateTime submittedAt,
            OffsetDateTime completedAt,
            int totalQuestions,
            int fullySolvedQuestions,
            List<SubmissionResultResponse> results
    ) {
        this.id = id;
        this.sessionCode = sessionCode;
        this.status = status;
        this.submittedAt = submittedAt;
        this.completedAt = completedAt;
        this.totalQuestions = totalQuestions;
        this.fullySolvedQuestions = fullySolvedQuestions;
        if (results != null) {
            this.results = results;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getFullySolvedQuestions() {
        return fullySolvedQuestions;
    }

    public void setFullySolvedQuestions(int fullySolvedQuestions) {
        this.fullySolvedQuestions = fullySolvedQuestions;
    }

    public List<SubmissionResultResponse> getResults() {
        return results;
    }

    public void setResults(List<SubmissionResultResponse> results) {
        this.results = results;
    }
}
