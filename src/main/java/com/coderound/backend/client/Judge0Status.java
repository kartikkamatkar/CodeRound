package com.coderound.backend.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Judge0Status {

    private int id;
    private String description;

    public Judge0Status() {
    }

    public Judge0Status(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
