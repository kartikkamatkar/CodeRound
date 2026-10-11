package com.coderound.backend.controller;

import com.coderound.backend.dto.AssessmentCreateRequest;
import com.coderound.backend.dto.CodeSaveRequest;
import com.coderound.backend.entity.AssessmentSession;
import com.coderound.backend.entity.Question;
import com.coderound.backend.enums.AssessmentStatus;
import com.coderound.backend.repository.AssessmentSessionRepository;
import com.coderound.backend.service.AssessmentSessionService;
import com.coderound.backend.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AssessmentSessionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AssessmentSessionService sessionService;

    @Autowired
    private AssessmentSessionRepository sessionRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Long> seededQuestionIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        seededQuestionIds.clear();

        for (int i = 1; i <= 4; i++) {
            Question q = new Question();
            q.setTitle("Round Question " + i);
            q.setDescription("Description for question " + i);
            q.setDifficulty(i % 2 == 0 ? "Medium" : "Easy");
            Question saved = questionService.createQuestion(q);
            seededQuestionIds.add(saved.getId());
        }
    }

    @Test
    @DisplayName("POST /api/v1/assessments creates a session with 4 distinct questions")
    void testCreateAssessmentSession() throws Exception {
        AssessmentCreateRequest req = new AssessmentCreateRequest(seededQuestionIds);
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/api/v1/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionCode", startsWith("CR-")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.questions", hasSize(4)))
                .andExpect(jsonPath("$.questions[0].questionOrder", is(1)))
                .andExpect(jsonPath("$.questions[3].questionOrder", is(4)));
    }

    @Test
    @DisplayName("GET /api/v1/assessments/{sessionCode} returns session details and ordered questions")
    void testGetAssessmentSession() throws Exception {
        AssessmentSession session = sessionService.createSession(seededQuestionIds);

        mockMvc.perform(get("/api/v1/assessments/" + session.getSessionCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionCode", is(session.getSessionCode())))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.questions", hasSize(4)))
                .andExpect(jsonPath("$.remainingSeconds", greaterThan(0)));
    }

    @Test
    @DisplayName("GET /api/v1/assessments/{sessionCode}/questions/{questionId} returns question detail")
    void testGetAssignedQuestion() throws Exception {
        AssessmentSession session = sessionService.createSession(seededQuestionIds);
        Long questionId = seededQuestionIds.get(0);

        mockMvc.perform(get("/api/v1/assessments/" + session.getSessionCode() + "/questions/" + questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(questionId.intValue())))
                .andExpect(jsonPath("$.title", is("Round Question 1")))
                .andExpect(jsonPath("$.questionOrder", is(1)))
                .andExpect(jsonPath("$.savedLanguage", is("JAVA")))
                .andExpect(jsonPath("$.savedSourceCode", notNullValue()));
    }

    @Test
    @DisplayName("PUT and GET /api/v1/assessments/{sessionCode}/questions/{questionId}/code saves and retrieves code")
    void testSaveAndGetCode() throws Exception {
        AssessmentSession session = sessionService.createSession(seededQuestionIds);
        Long questionId = seededQuestionIds.get(1);

        CodeSaveRequest saveReq = new CodeSaveRequest("JAVA", "class Solution { int sum() { return 0; } }");
        String json = objectMapper.writeValueAsString(saveReq);

        mockMvc.perform(put("/api/v1/assessments/" + session.getSessionCode() + "/questions/" + questionId + "/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId", is(questionId.intValue())))
                .andExpect(jsonPath("$.language", is("JAVA")))
                .andExpect(jsonPath("$.sourceCode", containsString("class Solution")));

        mockMvc.perform(get("/api/v1/assessments/" + session.getSessionCode() + "/questions/" + questionId + "/code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceCode", containsString("class Solution")));
    }

    @Test
    @DisplayName("Saving code for an unassigned question ID returns 400 Bad Request")
    void testSaveCodeForUnassignedQuestion() throws Exception {
        AssessmentSession session = sessionService.createSession(seededQuestionIds);

        Question otherQ = new Question();
        otherQ.setTitle("Other Question");
        otherQ.setDescription("Not in round");
        otherQ.setDifficulty("Easy");
        Question savedOther = questionService.createQuestion(otherQ);

        CodeSaveRequest saveReq = new CodeSaveRequest("JAVA", "int x = 1;");
        String json = objectMapper.writeValueAsString(saveReq);

        mockMvc.perform(put("/api/v1/assessments/" + session.getSessionCode() + "/questions/" + savedOther.getId() + "/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Saving code on an expired session returns 409 Conflict")
    void testSaveCodeOnExpiredSession() throws Exception {
        AssessmentSession session = sessionService.createSession(seededQuestionIds);
        session.setExpiresAt(OffsetDateTime.now().minusMinutes(5));
        session.setStatus(AssessmentStatus.EXPIRED);
        sessionRepository.save(session);

        CodeSaveRequest saveReq = new CodeSaveRequest("JAVA", "int x = 1;");
        String json = objectMapper.writeValueAsString(saveReq);

        mockMvc.perform(put("/api/v1/assessments/" + session.getSessionCode() + "/questions/" + seededQuestionIds.get(0) + "/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }
}
