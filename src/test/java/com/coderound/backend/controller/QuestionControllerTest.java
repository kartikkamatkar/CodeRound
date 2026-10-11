package com.coderound.backend.controller;

import com.coderound.backend.dto.QuestionCreateRequest;
import com.coderound.backend.dto.TestCaseCreateRequest;
import com.coderound.backend.entity.Question;
import com.coderound.backend.entity.TestCase;
import com.coderound.backend.service.QuestionService;
import com.coderound.backend.service.TestCaseService;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class QuestionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestCaseService testCaseService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("GET /api/v1/questions returns all questions")
    void testGetAllQuestions() throws Exception {
        Question q = new Question();
        q.setTitle("Sample Question 1");
        q.setDescription("Desc 1");
        q.setDifficulty("Easy");
        questionService.createQuestion(q);

        mockMvc.perform(get("/api/v1/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @DisplayName("GET /api/v1/questions?difficulty=Easy filters by difficulty")
    void testGetQuestionsByDifficulty() throws Exception {
        Question q1 = new Question();
        q1.setTitle("Easy Q");
        q1.setDescription("Desc");
        q1.setDifficulty("Easy");
        questionService.createQuestion(q1);

        Question q2 = new Question();
        q2.setTitle("Hard Q");
        q2.setDescription("Desc");
        q2.setDifficulty("Hard");
        questionService.createQuestion(q2);

        mockMvc.perform(get("/api/v1/questions?difficulty=Easy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].difficulty", everyItem(is("Easy"))));
    }

    @Test
    @DisplayName("GET /api/v1/questions/{id} returns question with only sample test cases")
    void testGetQuestionByIdExcludesHiddenTestCases() throws Exception {
        Question q = new Question();
        q.setTitle("Palindrome Check");
        q.setDescription("Check if palindrome");
        q.setDifficulty("Easy");
        Question saved = questionService.createQuestion(q);

        TestCase sample = new TestCase();
        sample.setInput("aba");
        sample.setExpectedOutput("true");
        sample.setHidden(false);
        testCaseService.createTestCase(saved.getId(), sample);

        TestCase hidden = new TestCase();
        hidden.setInput("racecar");
        hidden.setExpectedOutput("true");
        hidden.setHidden(true);
        testCaseService.createTestCase(saved.getId(), hidden);

        mockMvc.perform(get("/api/v1/questions/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Palindrome Check")))
                .andExpect(jsonPath("$.sampleTestCases", hasSize(1)))
                .andExpect(jsonPath("$.sampleTestCases[0].input", is("aba")));
    }

    @Test
    @DisplayName("POST /api/v1/questions creates a new question and returns 201 CREATED")
    void testCreateQuestion() throws Exception {
        QuestionCreateRequest req = new QuestionCreateRequest();
        req.setTitle("Fibonacci Number");
        req.setDescription("Find nth fibonacci");
        req.setDifficulty("Easy");
        req.setTestCases(List.of(
                new TestCaseCreateRequest("2", "1", false),
                new TestCaseCreateRequest("3", "2", true)
        ));

        // Note: Spring Boot 4 / Jackson 3 uses ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(req);

        mockMvc.perform(post("/api/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Fibonacci Number")))
                .andExpect(jsonPath("$.sampleTestCases", hasSize(1)));
    }
}
