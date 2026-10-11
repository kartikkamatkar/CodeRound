package com.coderound.backend.repository;

import com.coderound.backend.entity.AssessmentSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentSessionRepository extends JpaRepository<AssessmentSession,Long>
{
    Optional<AssessmentSession> findBySessionCode(String sessionCode);

    boolean existsBySessionCode(String sessionCode);
}
