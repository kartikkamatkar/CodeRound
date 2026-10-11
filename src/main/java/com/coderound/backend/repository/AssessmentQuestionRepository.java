package com.coderound.backend.repository;

import com.coderound.backend.entity.AssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, Long> {

    boolean existsByAssessmentSession_IdAndQuestion_Id(Long sessionId, Long questionId);

    List<AssessmentQuestion> findByAssessmentSession_IdOrderByQuestionOrderAsc(Long sessionId);

    Optional<AssessmentQuestion> findByAssessmentSession_IdAndQuestion_Id(Long sessionId, Long questionId);

    List<AssessmentQuestion> findByAssessmentSession_SessionCodeOrderByQuestionOrderAsc(String sessionCode);

    Optional<AssessmentQuestion> findByAssessmentSession_SessionCodeAndQuestion_Id(String sessionCode, Long questionId);
}
