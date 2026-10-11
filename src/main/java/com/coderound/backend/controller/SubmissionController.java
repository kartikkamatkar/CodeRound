package com.coderound.backend.controller;

import com.coderound.backend.dto.SubmissionResponse;
import com.coderound.backend.service.SubmissionEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class SubmissionController {

    private final SubmissionEvaluationService evaluationService;

    public SubmissionController(SubmissionEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/api/v1/assessments/{sessionCode}/submit")
    public ResponseEntity<SubmissionResponse> submitAssessment(
            @PathVariable String sessionCode) {

        SubmissionResponse response = evaluationService.submitAndEvaluate(sessionCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/submissions/{submissionId}")
    public ResponseEntity<SubmissionResponse> getSubmissionById(
            @PathVariable Long submissionId) {

        SubmissionResponse response = evaluationService.getSubmissionDetails(submissionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/assessments/{sessionCode}/submission")
    public ResponseEntity<SubmissionResponse> getSubmissionBySessionCode(
            @PathVariable String sessionCode) {

        SubmissionResponse response = evaluationService.getSubmissionBySessionCode(sessionCode);
        return ResponseEntity.ok(response);
    }
}
