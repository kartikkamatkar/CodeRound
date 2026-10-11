package com.coderound.backend.controller;

import com.coderound.backend.dto.*;
import com.coderound.backend.entity.AssessmentQuestion;
import com.coderound.backend.entity.AssessmentSession;
import com.coderound.backend.entity.QuestionAttempt;
import com.coderound.backend.entity.TestCase;
import com.coderound.backend.service.AssessmentSessionService;
import com.coderound.backend.service.CodeExecutionService;
import com.coderound.backend.service.CodeTemplateService;
import com.coderound.backend.service.QuestionAttemptService;
import com.coderound.backend.service.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/assessments")
@CrossOrigin(origins = "*")
public class AssessmentSessionController {

    private final AssessmentSessionService sessionService;
    private final QuestionAttemptService attemptService;
    private final TestCaseService testCaseService;
    private final CodeTemplateService codeTemplateService;
    private final CodeExecutionService codeExecutionService;

    public AssessmentSessionController(
            AssessmentSessionService sessionService,
            QuestionAttemptService attemptService,
            TestCaseService testCaseService,
            CodeTemplateService codeTemplateService,
            CodeExecutionService codeExecutionService
    ) {
        this.sessionService = sessionService;
        this.attemptService = attemptService;
        this.testCaseService = testCaseService;
        this.codeTemplateService = codeTemplateService;
        this.codeExecutionService = codeExecutionService;
    }

    @PostMapping
    public ResponseEntity<AssessmentSessionResponse> createAssessment(
            @RequestBody(required = false) AssessmentCreateRequest request) {

        List<Long> questionIds = (request != null) ? request.getQuestionIds() : null;
        AssessmentSession session = sessionService.createSession(questionIds);
        List<AssessmentQuestion> assignedQuestions = sessionService.getAssignedQuestions(session.getSessionCode());

        AssessmentSessionResponse response = mapToSessionResponse(session, assignedQuestions);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{sessionCode}")
    public ResponseEntity<AssessmentSessionResponse> getAssessment(
            @PathVariable String sessionCode) {

        AssessmentSession session = sessionService.getSessionByCode(sessionCode);
        List<AssessmentQuestion> assignedQuestions = sessionService.getAssignedQuestions(sessionCode);

        AssessmentSessionResponse response = mapToSessionResponse(session, assignedQuestions);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionCode}/questions/{questionId}")
    public ResponseEntity<AssignedQuestionDetailResponse> getAssignedQuestion(
            @PathVariable String sessionCode,
            @PathVariable Long questionId) {

        AssessmentQuestion assigned = sessionService.getAssignedQuestion(sessionCode, questionId);
        List<TestCase> sampleCases = testCaseService.getVisibleTestCases(questionId);

        List<TestCaseSampleResponse> sampleResponses = sampleCases.stream()
                .map(tc -> new TestCaseSampleResponse(tc.getId(), tc.getInput(), tc.getExpectedOutput()))
                .collect(Collectors.toList());

        Optional<QuestionAttempt> attempt = attemptService.findAttemptOptional(sessionCode, questionId);
        String savedLanguage = attempt.map(QuestionAttempt::getLanguage).orElse("JAVA");
        String savedCode = attempt.map(QuestionAttempt::getSourceCode)
                .orElseGet(() -> codeTemplateService.getStarterCode(savedLanguage));

        AssignedQuestionDetailResponse response = new AssignedQuestionDetailResponse(
                assigned.getQuestion().getId(),
                assigned.getQuestion().getTitle(),
                assigned.getQuestion().getDescription(),
                assigned.getQuestion().getDifficulty(),
                assigned.getQuestionOrder(),
                sampleResponses,
                savedLanguage,
                savedCode
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sessionCode}/questions/{questionId}/code")
    public ResponseEntity<CodeResponse> saveCode(
            @PathVariable String sessionCode,
            @PathVariable Long questionId,
            @Valid @RequestBody CodeSaveRequest request) {

        QuestionAttempt attempt = attemptService.saveAttempt(
                sessionCode,
                questionId,
                request.getLanguage(),
                request.getSourceCode()
        );

        CodeResponse response = new CodeResponse(
                attempt.getQuestion().getId(),
                attempt.getLanguage(),
                attempt.getSourceCode(),
                attempt.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionCode}/questions/{questionId}/code")
    public ResponseEntity<CodeResponse> getCode(
            @PathVariable String sessionCode,
            @PathVariable Long questionId,
            @RequestParam(required = false, defaultValue = "JAVA") String language) {

        Optional<QuestionAttempt> attempt = attemptService.findAttemptOptional(sessionCode, questionId);

        if (attempt.isPresent()) {
            QuestionAttempt att = attempt.get();
            return ResponseEntity.ok(new CodeResponse(
                    att.getQuestion().getId(),
                    att.getLanguage(),
                    att.getSourceCode(),
                    att.getUpdatedAt()
            ));
        }

        // Return starter template if no attempt exists yet
        String starter = codeTemplateService.getStarterCode(language);
        return ResponseEntity.ok(new CodeResponse(
                questionId,
                language.toUpperCase(),
                starter,
                null
        ));
    }

    @PostMapping("/{sessionCode}/questions/{questionId}/run")
    public ResponseEntity<CodeRunResponse> runSampleCode(
            @PathVariable String sessionCode,
            @PathVariable Long questionId,
            @Valid @RequestBody CodeRunRequest request) {

        CodeRunResponse response = codeExecutionService.runSampleTests(
                sessionCode,
                questionId,
                request
        );

        return ResponseEntity.ok(response);
    }

    private AssessmentSessionResponse mapToSessionResponse(
            AssessmentSession session,
            List<AssessmentQuestion> assignedQuestions
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        long remaining = 0;
        if (session.getExpiresAt() != null && session.getExpiresAt().isAfter(now)) {
            remaining = Duration.between(now, session.getExpiresAt()).toSeconds();
        }

        List<AssignedQuestionSummary> questionSummaries = assignedQuestions.stream()
                .map(aq -> new AssignedQuestionSummary(
                        aq.getQuestion().getId(),
                        aq.getQuestion().getTitle(),
                        aq.getQuestion().getDifficulty(),
                        aq.getQuestionOrder()
                ))
                .collect(Collectors.toList());

        return new AssessmentSessionResponse(
                session.getSessionCode(),
                session.getStatus().name(),
                session.getStartedAt(),
                session.getExpiresAt(),
                remaining,
                questionSummaries
        );
    }
}
