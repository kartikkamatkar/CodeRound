package com.coderound.backend.dto;

import java.time.OffsetDateTime;

public class CodeResponse {

    private Long questionId;
    private String language;
    private String sourceCode;
    private OffsetDateTime updatedAt;

    public CodeResponse() {
    }

    public CodeResponse(Long questionId, String language, String sourceCode, OffsetDateTime updatedAt) {
        this.questionId = questionId;
        this.language = language;
        this.sourceCode = sourceCode;
        this.updatedAt = updatedAt;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
