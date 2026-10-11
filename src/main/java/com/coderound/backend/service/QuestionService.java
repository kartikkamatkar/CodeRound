package com.coderound.backend.service;

import com.coderound.backend.entity.Question;
import com.coderound.backend.exception.QuestionNotFoundException;
import com.coderound.backend.repository.QuestionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuestionService {

    private static final List<String> ALLOWED_DIFFICULTIES =
            List.of("Easy", "Medium", "Hard");

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question createQuestion(Question question) {

        if (question == null) {
            throw new IllegalArgumentException(
                    "Question cannot be null");
        }

        if (question.getTitle() == null
                || question.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Question title is required");
        }

        if (question.getTitle().length() > 255) {
            throw new IllegalArgumentException(
                    "Question title cannot exceed 255 characters");
        }

        if (question.getDescription() == null
                || question.getDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "Question description is required");
        }

        if (question.getDifficulty() == null
                || !ALLOWED_DIFFICULTIES.contains(
                question.getDifficulty())) {
            throw new IllegalArgumentException(
                    "Difficulty must be Easy, Medium, or Hard");
        }

        return questionRepository.save(question);
    }

    @Transactional(readOnly = true)
    public Question getQuestionById(Long questionId) {

        if (questionId == null || questionId <= 0) {
            throw new IllegalArgumentException(
                    "Question ID must be a positive number");
        }

        return questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new QuestionNotFoundException(
                                "Question not found with ID: " + questionId));
    }

    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsByDifficulty(String difficulty) {

        if (difficulty == null
                || !ALLOWED_DIFFICULTIES.contains(difficulty)) {
            throw new IllegalArgumentException(
                    "Difficulty must be Easy, Medium, or Hard");
        }

        return questionRepository.findByDifficulty(difficulty);
    }
}