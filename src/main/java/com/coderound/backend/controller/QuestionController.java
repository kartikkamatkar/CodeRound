package com.coderound.backend.controller;

import com.coderound.backend.dto.QuestionCreateRequest;
import com.coderound.backend.dto.QuestionDetailResponse;
import com.coderound.backend.dto.QuestionResponse;
import com.coderound.backend.dto.TestCaseCreateRequest;
import com.coderound.backend.dto.TestCaseSampleResponse;
import com.coderound.backend.entity.Question;
import com.coderound.backend.entity.TestCase;
import com.coderound.backend.service.QuestionService;
import com.coderound.backend.service.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    private final QuestionService questionService;
    private final TestCaseService testCaseService;

    public QuestionController(QuestionService questionService, TestCaseService testCaseService) {
        this.questionService = questionService;
        this.testCaseService = testCaseService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @RequestParam(required = false) String difficulty) {

        List<Question> questions = (difficulty != null && !difficulty.isBlank())
                ? questionService.getQuestionsByDifficulty(difficulty)
                : questionService.getAllQuestions();

        List<QuestionResponse> response = questions.stream()
                .map(q -> new QuestionResponse(q.getId(), q.getTitle(), q.getDescription(), q.getDifficulty()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDetailResponse> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        List<TestCase> sampleCases = testCaseService.getVisibleTestCases(id);

        List<TestCaseSampleResponse> sampleResponses = sampleCases.stream()
                .map(tc -> new TestCaseSampleResponse(tc.getId(), tc.getInput(), tc.getExpectedOutput()))
                .collect(Collectors.toList());

        QuestionDetailResponse detail = new QuestionDetailResponse(
                question.getId(),
                question.getTitle(),
                question.getDescription(),
                question.getDifficulty(),
                sampleResponses
        );

        return ResponseEntity.ok(detail);
    }

    @GetMapping("/{id}/test-cases")
    public ResponseEntity<List<TestCaseSampleResponse>> getSampleTestCases(@PathVariable Long id) {
        List<TestCase> sampleCases = testCaseService.getVisibleTestCases(id);

        List<TestCaseSampleResponse> sampleResponses = sampleCases.stream()
                .map(tc -> new TestCaseSampleResponse(tc.getId(), tc.getInput(), tc.getExpectedOutput()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(sampleResponses);
    }

    @PostMapping
    public ResponseEntity<QuestionDetailResponse> createQuestion(
            @Valid @RequestBody QuestionCreateRequest request) {

        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setDescription(request.getDescription());
        question.setDifficulty(request.getDifficulty());

        Question saved = questionService.createQuestion(question);

        if (request.getTestCases() != null) {
            for (TestCaseCreateRequest tcReq : request.getTestCases()) {
                TestCase testCase = new TestCase();
                testCase.setInput(tcReq.getInput());
                testCase.setExpectedOutput(tcReq.getExpectedOutput());
                testCase.setHidden(tcReq.isHidden());
                testCaseService.createTestCase(saved.getId(), testCase);
            }
        }

        List<TestCase> sampleCases = testCaseService.getVisibleTestCases(saved.getId());
        List<TestCaseSampleResponse> sampleResponses = sampleCases.stream()
                .map(tc -> new TestCaseSampleResponse(tc.getId(), tc.getInput(), tc.getExpectedOutput()))
                .collect(Collectors.toList());

        QuestionDetailResponse detail = new QuestionDetailResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getDifficulty(),
                sampleResponses
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(detail);
    }
}
