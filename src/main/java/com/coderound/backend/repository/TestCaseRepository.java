package com.coderound.backend.repository;

import com.coderound.backend.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase,Long> {

    List<TestCase> findByQuestion_Id(Long questionId);

    List<TestCase> findByQuestion_IdAndHiddenFalse(Long questionId);

    List<TestCase> findByQuestion_IdAndHiddenTrue(Long questionId);
}
