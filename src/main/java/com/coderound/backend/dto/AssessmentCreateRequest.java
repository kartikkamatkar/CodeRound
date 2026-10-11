package com.coderound.backend.dto;

import java.util.List;

public class AssessmentCreateRequest {

    private List<Long> questionIds;

    public AssessmentCreateRequest() {
    }

    public AssessmentCreateRequest(List<Long> questionIds) {
        this.questionIds = questionIds;
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }
}
