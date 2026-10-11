
package com.coderound.backend.dto;

import java.time.OffsetDateTime;

public class ApiErrorResponse {

    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String path;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(
            OffsetDateTime timestamp,
            int status,
            String error,
            String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}