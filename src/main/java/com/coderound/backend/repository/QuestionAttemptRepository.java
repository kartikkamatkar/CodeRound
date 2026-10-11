package com.coderound.backend.repository;

import com.coderound.backend.entity.QuestionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt,Long> {
    Optional<QuestionAttempt> findByAssessmentSession_IdAndQuestion_Id(
            Long sessionId, Long questionId);

    Optional<QuestionAttempt> findByAssessmentSession_SessionCodeAndQuestion_Id(
            String sessionCode, Long questionId);

    List<QuestionAttempt> findByAssessmentSession_Id(Long sessionId);
}
