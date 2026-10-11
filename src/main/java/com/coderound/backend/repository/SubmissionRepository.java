package com.coderound.backend.repository;

import com.coderound.backend.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {

    boolean existsByAssessmentSession_Id(Long sessionId);

    Optional<Submission> findByAssessmentSession_Id(Long sessionId);
}
