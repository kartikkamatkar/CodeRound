package com.coderound.backend.dto;

public class AssignedQuestionSummary {

    private Long id;
    private String title;
    private String difficulty;
    private Integer questionOrder;

    public AssignedQuestionSummary() {
    }

    public AssignedQuestionSummary(Long id, String title, String difficulty, Integer questionOrder) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.questionOrder = questionOrder;
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
}
