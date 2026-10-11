package com.coderound.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "judge0")
public class Judge0Properties {

    private String baseUrl = "http://localhost:2358";
    private String apiKey = "";
    private int timeoutSeconds = 15;
    private int pollIntervalMs = 500;
    private boolean mockWhenUnavailable = true;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getPollIntervalMs() {
        return pollIntervalMs;
    }

    public void setPollIntervalMs(int pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public boolean isMockWhenUnavailable() {
        return mockWhenUnavailable;
    }

    public void setMockWhenUnavailable(boolean mockWhenUnavailable) {
        this.mockWhenUnavailable = mockWhenUnavailable;
    }
}
