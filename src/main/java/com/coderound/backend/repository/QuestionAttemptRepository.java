package com.coderound.backend.repository;

import com.coderound.backend.entity.QuestionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt,Long> {
    Optional<QuestionAttempt> findByAssessmentSession_IdAndQuestion_Id(
            Long sessionId, Long questionId);
}
