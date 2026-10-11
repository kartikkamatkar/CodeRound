package com.coderound.backend.repository;

import com.coderound.backend.entity.SubmissionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionResultRepository extends JpaRepository<SubmissionResult,Long> {

    Optional<SubmissionResult> findBySubmission_IdAndQuestion_Id(
            Long submissionId,
            Long questionId);

    List<SubmissionResult> findBySubmission_IdOrderByQuestion_IdAsc(
            Long submissionId);
}
