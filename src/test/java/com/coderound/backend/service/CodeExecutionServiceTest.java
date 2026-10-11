package com.coderound.backend.service;

import com.coderound.backend.dto.CodeRunRequest;
import com.coderound.backend.dto.CodeRunResponse;
import com.coderound.backend.entity.AssessmentSession;
import com.coderound.backend.entity.Question;
import com.coderound.backend.entity.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CodeExecutionServiceTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private AssessmentSessionService sessionService;

    @Autowired
    private CodeExecutionService executionService;

    private AssessmentSession session;
    private Long questionId;

    @BeforeEach
    void setUp() {
        List<Long> qIds = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Question q = new Question();
            q.setTitle("Exec Question " + i);
            q.setDescription("Desc " + i);
            q.setDifficulty("Easy");
            Question saved = questionService.createQuestion(q);
            qIds.add(saved.getId());

            TestCase tc1 = new TestCase();
            tc1.setInput("input" + i);
            tc1.setExpectedOutput("output" + i);
            tc1.setHidden(false);
            testCaseService.createTestCase(saved.getId(), tc1);

            TestCase tc2 = new TestCase();
            tc2.setInput("hidden" + i);
            tc2.setExpectedOutput("hidden_out" + i);
            tc2.setHidden(true);
            testCaseService.createTestCase(saved.getId(), tc2);
        }

        session = sessionService.createSession(qIds);
        questionId = qIds.get(0);
    }

    @Test
    @DisplayName("Run code evaluates visible sample test cases and returns ACCEPTED")
    void testRunCodeAccepted() {
        CodeRunRequest req = new CodeRunRequest("JAVA", "public class Main { public static void main(String[] args) {} }");
        CodeRunResponse response = executionService.runSampleTests(session.getSessionCode(), questionId, req);

        assertNotNull(response);
        assertEquals("ACCEPTED", response.getStatus());
        assertEquals(1, response.getTotalTestCases()); // Only 1 visible test case
        assertEquals(1, response.getPassedTestCases());
        assertTrue(response.getTestCaseResults().get(0).isPassed());
    }

    @Test
    @DisplayName("Run code handles compilation error gracefully")
    void testRunCodeCompilationError() {
        CodeRunRequest req = new CodeRunRequest("JAVA", "class Solution { SYNTAX_ERROR }");
        CodeRunResponse response = executionService.runSampleTests(session.getSessionCode(), questionId, req);

        assertNotNull(response);
        assertEquals("COMPILATION_ERROR", response.getStatus());
        assertEquals(0, response.getPassedTestCases());
        assertNotNull(response.getTestCaseResults().get(0).getCompileOutput());
    }

    @Test
    @DisplayName("Run code handles runtime error gracefully")
    void testRunCodeRuntimeError() {
        CodeRunRequest req = new CodeRunRequest("JAVA", "class Solution { RUNTIME_ERROR }");
        CodeRunResponse response = executionService.runSampleTests(session.getSessionCode(), questionId, req);

        assertNotNull(response);
        assertEquals("RUNTIME_ERROR", response.getStatus());
        assertEquals(0, response.getPassedTestCases());
    }

    @Test
    @DisplayName("Run code handles time limit exceeded gracefully")
    void testRunCodeTimeout() {
        CodeRunRequest req = new CodeRunRequest("JAVA", "class Solution { TIMEOUT }");
        CodeRunResponse response = executionService.runSampleTests(session.getSessionCode(), questionId, req);

        assertNotNull(response);
        assertEquals("TIME_LIMIT_EXCEEDED", response.getStatus());
        assertEquals(0, response.getPassedTestCases());
    }
}
