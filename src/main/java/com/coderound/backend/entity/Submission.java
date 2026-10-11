package com.coderound.backend.entity;

import com.coderound.backend.enums.SubmissionStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name ="submissions",uniqueConstraints = {
        @UniqueConstraint(columnNames ="assessment_session_id")
})
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_session_id",nullable = false)
    private AssessmentSession assessmentSession;

    @Enumerated(EnumType.STRING)
    @Column(name ="status", nullable = false ,length = 20)
    private SubmissionStatus status;

    @Column(name ="submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (this.submittedAt == null) {
            this.submittedAt = OffsetDateTime.now();
        }
    }

    public Submission(){

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

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }
    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
