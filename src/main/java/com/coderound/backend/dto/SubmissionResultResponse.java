package com.coderound.backend.dto;

public class SubmissionResultResponse {

    private Long id;
    private Long questionId;
    private String questionTitle;
    private String difficulty;
    private String language;
    private Integer totalTestCases;
    private Integer passedTestCases;
    private String status;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;

    public SubmissionResultResponse() {
    }

    public SubmissionResultResponse(
            Long id,
            Long questionId,
            String questionTitle,
            String difficulty,
            String language,
            Integer totalTestCases,
            Integer passedTestCases,
            String status,
            Integer executionTimeMs,
            Integer memoryUsedKb
    ) {
        this.id = id;
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.difficulty = difficulty;
        this.language = language;
        this.totalTestCases = totalTestCases;
        this.passedTestCases = passedTestCases;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
        this.memoryUsedKb = memoryUsedKb;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(Integer totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public Integer getPassedTestCases() {
        return passedTestCases;
    }

    public void setPassedTestCases(Integer passedTestCases) {
        this.passedTestCases = passedTestCases;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public Integer getMemoryUsedKb() {
        return memoryUsedKb;
    }

    public void setMemoryUsedKb(Integer memoryUsedKb) {
        this.memoryUsedKb = memoryUsedKb;
    }
}
