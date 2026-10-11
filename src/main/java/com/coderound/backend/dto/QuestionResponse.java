
package com.coderound.backend.dto;

public class QuestionResponse {

    private Long id;
    private String title;
    private String description;
    private String difficulty;

    public QuestionResponse() {
    }

    public QuestionResponse(
            Long id,
            String title,
            String description,
            String difficulty
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
