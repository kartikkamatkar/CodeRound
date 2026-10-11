package com.coderound.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class CodeRunResponse {

    private String status;
    private int totalTestCases;
    private int passedTestCases;
    private Double totalExecutionTime;
    private Integer peakMemory;
    private List<TestCaseResultItem> testCaseResults = new ArrayList<>();

    public CodeRunResponse() {
    }

    public CodeRunResponse(
            String status,
            int totalTestCases,
            int passedTestCases,
            Double totalExecutionTime,
            Integer peakMemory,
            List<TestCaseResultItem> testCaseResults
    ) {
        this.status = status;
        this.totalTestCases = totalTestCases;
        this.passedTestCases = passedTestCases;
        this.totalExecutionTime = totalExecutionTime;
        this.peakMemory = peakMemory;
        if (testCaseResults != null) {
            this.testCaseResults = testCaseResults;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public int getPassedTestCases() {
        return passedTestCases;
    }

    public void setPassedTestCases(int passedTestCases) {
        this.passedTestCases = passedTestCases;
    }

    public Double getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public void setTotalExecutionTime(Double totalExecutionTime) {
        this.totalExecutionTime = totalExecutionTime;
    }

    public Integer getPeakMemory() {
        return peakMemory;
    }

    public void setPeakMemory(Integer peakMemory) {
        this.peakMemory = peakMemory;
    }

    public List<TestCaseResultItem> getTestCaseResults() {
        return testCaseResults;
    }

    public void setTestCaseResults(List<TestCaseResultItem> testCaseResults) {
        this.testCaseResults = testCaseResults;
    }
}
