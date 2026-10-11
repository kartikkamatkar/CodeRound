package com.coderound.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class TestCaseCreateRequest {

    @NotBlank(message = "Input is required")
    private String input;

    @NotBlank(message = "Expected output is required")
    private String expectedOutput;

    private boolean hidden = false;

    public TestCaseCreateRequest() {
    }

    public TestCaseCreateRequest(String input, String expectedOutput, boolean hidden) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.hidden = hidden;
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
