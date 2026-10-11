package com.coderound.backend.repository;

import com.coderound.backend.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase,Long> {

    List<TestCase> findByQuestion_Id(Long questionId);

    List<TestCase> findByQuestion_IdAndIsHiddenFalse(Long questionId);

    List<TestCase> findByQuestion_IdAndIsHiddenTrue(Long questionId);

}
