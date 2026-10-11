package com.coderound.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CodeSaveRequest {

    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Source code is required")
    @Size(max = 100_000, message = "Source code cannot exceed 100,000 characters")
    private String sourceCode;

    public CodeSaveRequest() {
    }

    public CodeSaveRequest(String language, String sourceCode) {
        this.language = language;
        this.sourceCode = sourceCode;
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
}
