package com.coderound.backend.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Judge0SubmissionRequest {

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private int languageId;

    @JsonProperty("stdin")
    private String stdin;

    @JsonProperty("expected_output")
    private String expectedOutput;

    @JsonProperty("cpu_time_limit")
    private Double cpuTimeLimit = 5.0;

    @JsonProperty("memory_limit")
    private Integer memoryLimit = 128000;

    public Judge0SubmissionRequest() {
    }

    public Judge0SubmissionRequest(String sourceCode, int languageId, String stdin, String expectedOutput) {
        this.sourceCode = sourceCode;
        this.languageId = languageId;
        this.stdin = stdin;
        this.expectedOutput = expectedOutput;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getStdin() {
        return stdin;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public Double getCpuTimeLimit() {
        return cpuTimeLimit;
    }

    public void setCpuTimeLimit(Double cpuTimeLimit) {
        this.cpuTimeLimit = cpuTimeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }
}
