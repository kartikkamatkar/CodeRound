package com.coderound.backend.repository;

import com.coderound.backend.entity.AssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion,Long> {
    boolean existsByAssessmentSession_IdAndQuestion_Id(
            Long sessionId, Long questionId);
}
