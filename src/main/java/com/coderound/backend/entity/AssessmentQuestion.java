package com.coderound.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.OffsetDateTime;

@Entity
@Table(name="assessment_questions",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"assessment_session_id","question_id"}),
        @UniqueConstraint(columnNames = {"assessment_session_id","question_order"}
)
}
)
public class AssessmentQuestion {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_session_id",nullable = false)
    private AssessmentSession assessmentSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="question_id",nullable = false)
    private Question question;

    @Column(name ="question_order",nullable = false)
    @Min(1)
    @Max(4)
    private Integer questionOrder;

    @Column(name = "created_at",nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }

    public AssessmentQuestion(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssessmentSession getAssessmentSession() {
        return assessmentSession;
    }

    public void setAssessmentSession(AssessmentSession assessmentSession) {
        this.assessmentSession = assessmentSession;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
