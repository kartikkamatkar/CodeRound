package com.coderound.backend.controller;

import com.coderound.backend.entity.AssessmentSession;
import com.coderound.backend.entity.Question;
import com.coderound.backend.entity.TestCase;
import com.coderound.backend.enums.AssessmentStatus;
import com.coderound.backend.repository.AssessmentSessionRepository;
import com.coderound.backend.service.AssessmentSessionService;
import com.coderound.backend.service.QuestionAttemptService;
import com.coderound.backend.service.QuestionService;
import com.coderound.backend.service.TestCaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class SubmissionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private AssessmentSessionService sessionService;

    @Autowired
    private AssessmentSessionRepository sessionRepository;

    @Autowired
    private QuestionAttemptService attemptService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Long> questionIds = new ArrayList<>();
    private AssessmentSession session;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        questionIds.clear();

        for (int i = 1; i <= 4; i++) {
            Question q = new Question();
            q.setTitle("Final Round Q" + i);
            q.setDescription("Problem statement " + i);
            q.setDifficulty(i == 4 ? "Hard" : (i == 3 ? "Medium" : "Easy"));
            Question saved = questionService.createQuestion(q);
            questionIds.add(saved.getId());

            TestCase sample = new TestCase();
            sample.setInput("in_" + i);
            sample.setExpectedOutput("out_" + i);
            sample.setHidden(false);
            testCaseService.createTestCase(saved.getId(), sample);

            TestCase hidden = new TestCase();
            hidden.setInput("hidden_in_" + i);
            hidden.setExpectedOutput("hidden_out_" + i);
            hidden.setHidden(true);
            testCaseService.createTestCase(saved.getId(), hidden);
        }

        session = sessionService.createSession(questionIds);

        // Save attempts for the first two questions
        attemptService.saveAttempt(session.getSessionCode(), questionIds.get(0), "JAVA", "class Solution {}");
        attemptService.saveAttempt(session.getSessionCode(), questionIds.get(1), "PYTHON", "def solve(): pass");
    }

    @Test
    @DisplayName("POST /api/v1/assessments/{sessionCode}/submit evaluates all 4 questions and completes submission")
    void testSubmitAssessment() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/assessments/" + session.getSessionCode() + "/submit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionCode", is(session.getSessionCode())))
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.totalQuestions", is(4)))
                .andExpect(jsonPath("$.results", hasSize(4)))
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        long submissionId = root.get("id").asLong();

        // Verify retrieval by submissionId
        mockMvc.perform(get("/api/v1/submissions/" + submissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) submissionId)))
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.results", hasSize(4)));
    }

    @Test
    @DisplayName("Duplicate submission returns 409 Conflict")
    void testDuplicateSubmissionRejected() throws Exception {
        mockMvc.perform(post("/api/v1/assessments/" + session.getSessionCode() + "/submit"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/assessments/" + session.getSessionCode() + "/submit"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Submitting an expired session returns 409 Conflict")
    void testExpiredSubmissionRejected() throws Exception {
        session.setExpiresAt(OffsetDateTime.now().minusMinutes(10));
        session.setStatus(AssessmentStatus.EXPIRED);
        sessionRepository.save(session);

        mockMvc.perform(post("/api/v1/assessments/" + session.getSessionCode() + "/submit"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/v1/submissions/999999 returns 404 Not Found")
    void testGetNonExistentSubmission() throws Exception {
        mockMvc.perform(get("/api/v1/submissions/999999"))
                .andExpect(status().isNotFound());
    }
}
