package com.coderound.backend.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Judge0SubmissionResponse {

    private String token;
    private String stdout;
    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    private String message;
    private String time;
    private Integer memory;
    private Judge0Status status;

    public Judge0SubmissionResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getCompileOutput() {
        return compileOutput;
    }

    public void setCompileOutput(String compileOutput) {
        this.compileOutput = compileOutput;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public Judge0Status getStatus() {
        return status;
    }

    public void setStatus(Judge0Status status) {
        this.status = status;
    }
}
