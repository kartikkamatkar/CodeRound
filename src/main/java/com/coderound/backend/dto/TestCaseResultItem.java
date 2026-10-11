package com.coderound.backend.dto;

public class TestCaseResultItem {

    private Long testCaseId;
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private boolean passed;
    private String status;
    private String compileOutput;
    private String error;
    private Double executionTime;
    private Integer memoryUsed;

    public TestCaseResultItem() {
    }

    public TestCaseResultItem(
            Long testCaseId,
            String input,
            String expectedOutput,
            String actualOutput,
            boolean passed,
            String status,
            String compileOutput,
            String error,
            Double executionTime,
            Integer memoryUsed
    ) {
        this.testCaseId = testCaseId;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
        this.passed = passed;
        this.status = status;
        this.compileOutput = compileOutput;
        this.error = error;
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public String getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(String actualOutput) {
        this.actualOutput = actualOutput;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompileOutput() {
        return compileOutput;
    }

    public void setCompileOutput(String compileOutput) {
        this.compileOutput = compileOutput;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Double executionTime) {
        this.executionTime = executionTime;
    }

    public Integer getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(Integer memoryUsed) {
        this.memoryUsed = memoryUsed;
    }
}
