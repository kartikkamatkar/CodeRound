package com.coderound.backend.service;

import com.coderound.backend.entity.Question;
import com.coderound.backend.entity.TestCase;
import com.coderound.backend.exception.QuestionNotFoundException;
import com.coderound.backend.repository.QuestionRepository;
import com.coderound.backend.repository.TestCaseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TestCaseService {

    private static final int MAX_INPUT_LENGTH = 100_000;
    private static final int MAX_OUTPUT_LENGTH = 100_000;

    private final QuestionRepository questionRepository;
    private final TestCaseRepository testCaseRepository;

    public TestCaseService(
            QuestionRepository questionRepository,
            TestCaseRepository testCaseRepository) {

        this.questionRepository = questionRepository;
        this.testCaseRepository = testCaseRepository;
    }

    public TestCase createTestCase(Long questionId, TestCase testCase) {

        validateQuestionId(questionId);

        if (testCase == null) {
            throw new IllegalArgumentException(
                    "Test case cannot be null");
        }

        if (testCase.getInput() == null
                || testCase.getInput().isBlank()) {
            throw new IllegalArgumentException(
                    "Test case input is required");
        }

        if (testCase.getInput().length() > MAX_INPUT_LENGTH) {
            throw new IllegalArgumentException(
                    "Test case input is too long");
        }

        if (testCase.getExpectedOutput() == null
                || testCase.getExpectedOutput().isBlank()) {
            throw new IllegalArgumentException(
                    "Expected output is required");
        }

        if (testCase.getExpectedOutput().length() > MAX_OUTPUT_LENGTH) {
            throw new IllegalArgumentException(
                    "Expected output is too long");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new QuestionNotFoundException(
                                "Question not found: " + questionId));

        testCase.setQuestion(question);

        return testCaseRepository.save(testCase);
    }

    @Transactional(readOnly = true)
    public List<TestCase> getVisibleTestCases(Long questionId) {

        validateQuestionExists(questionId);

        return testCaseRepository
                .findByQuestion_IdAndIsHiddenFalse(questionId);
    }

    @Transactional(readOnly = true)
    public List<TestCase> getHiddenTestCases(Long questionId) {

        validateQuestionExists(questionId);

        return testCaseRepository
                .findByQuestion_IdAndIsHiddenTrue(questionId);
    }

    @Transactional(readOnly = true)
    public List<TestCase> getAllTestCasesForEvaluation(Long questionId) {

        validateQuestionExists(questionId);

        return testCaseRepository.findByQuestion_Id(questionId);
    }

    private void validateQuestionExists(Long questionId) {

        validateQuestionId(questionId);

        if (!questionRepository.existsById(questionId)) {
            throw new QuestionNotFoundException(
                    "Question not found: " + questionId);
        }
    }

    private void validateQuestionId(Long questionId) {

        if (questionId == null || questionId <= 0) {
            throw new IllegalArgumentException(
                    "Question ID must be a positive number");
        }
    }
}