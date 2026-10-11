package com.coderound.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class AssignedQuestionDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private Integer questionOrder;
    private List<TestCaseSampleResponse> sampleTestCases = new ArrayList<>();
    private String savedLanguage;
    private String savedSourceCode;

    public AssignedQuestionDetailResponse() {
    }

    public AssignedQuestionDetailResponse(
            Long id,
            String title,
            String description,
            String difficulty,
            Integer questionOrder,
            List<TestCaseSampleResponse> sampleTestCases,
            String savedLanguage,
            String savedSourceCode
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.questionOrder = questionOrder;
        if (sampleTestCases != null) {
            this.sampleTestCases = sampleTestCases;
        }
        this.savedLanguage = savedLanguage;
        this.savedSourceCode = savedSourceCode;
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

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public List<TestCaseSampleResponse> getSampleTestCases() {
        return sampleTestCases;
    }

    public void setSampleTestCases(List<TestCaseSampleResponse> sampleTestCases) {
        this.sampleTestCases = sampleTestCases;
    }

    public String getSavedLanguage() {
        return savedLanguage;
    }

    public void setSavedLanguage(String savedLanguage) {
        this.savedLanguage = savedLanguage;
    }

    public String getSavedSourceCode() {
        return savedSourceCode;
    }

    public void setSavedSourceCode(String savedSourceCode) {
        this.savedSourceCode = savedSourceCode;
    }
}
