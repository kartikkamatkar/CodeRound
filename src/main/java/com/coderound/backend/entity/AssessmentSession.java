package com.coderound.backend.entity;

import com.coderound.backend.enums.AssessmentStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table( name = "assessment_sessions")
public class AssessmentSession {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="session_code",nullable = false,unique = true,length = 20)
    private String sessionCode ;

    @Enumerated(EnumType.STRING)
    @Column(name ="status",nullable = false,length = 20)
    private AssessmentStatus status;

    @Column(name = "started_at",nullable = false)
    private OffsetDateTime startedAt;

    @Column(name ="expires_at",nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name ="submitted_at")
    private OffsetDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        if (this.startedAt == null) {
            this.startedAt = OffsetDateTime.now();
        }
        if (this.expiresAt == null) {
            this.expiresAt = this.startedAt.plusMinutes(60);
        }
    }

    public AssessmentSession(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
