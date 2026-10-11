package com.coderound.backend.repository;

import com.coderound.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Long>{
    List<Question> findByDifficulty(String difficulty);
}
