package com.coderound.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class QuestionDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private List<TestCaseSampleResponse> sampleTestCases = new ArrayList<>();

    public QuestionDetailResponse() {
    }

    public QuestionDetailResponse(
            Long id,
            String title,
            String description,
            String difficulty,
            List<TestCaseSampleResponse> sampleTestCases
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        if (sampleTestCases != null) {
            this.sampleTestCases = sampleTestCases;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<TestCaseSampleResponse> getSampleTestCases() {
        return sampleTestCases;
    }

    public void setSampleTestCases(List<TestCaseSampleResponse> sampleTestCases) {
        this.sampleTestCases = sampleTestCases;
    }
}
