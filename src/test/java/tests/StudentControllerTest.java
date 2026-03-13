package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.se3010_testing.Se3010TestingApplication;
import com.sliit.se3010_testing.model.Student;
import com.sliit.se3010_testing.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Member 1 – TestNG Assertions using MockMvc (no JUnit, no Selenium, no Postman)
 */
@SpringBootTest(classes = Se3010TestingApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeMethod
    public void cleanUp() {
        studentRepository.deleteAll();
    }

    // ── 201 Created ──────────────────────────────────────────────────────────
    @Test
    public void testCreateStudent_Returns201AndCorrectBody() throws Exception {
        Student student = Student.builder()
                .name("Alice").email("alice@test.com").age(22).course("SE3010").build();

        String body = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@test.com"))
                .andReturn().getResponse().getContentAsString();

        Assert.assertFalse(body.isEmpty(), "Response body must not be empty");
    }

    // ── 200 OK ───────────────────────────────────────────────────────────────
    @Test
    public void testGetStudent_Returns200() throws Exception {
        Student saved = studentRepository.save(
                Student.builder().name("Bob").email("bob@test.com").age(23).course("SE3020").build());

        String body = mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andReturn().getResponse().getContentAsString();

        Assert.assertNotNull(body, "Response body should not be null");
    }

    // ── 404 Not Found ─────────────────────────────────────────────────────────
    @Test
    public void testGetStudent_NotFound_Returns404() throws Exception {
        int statusCode = mockMvc.perform(get("/api/students/{id}", 99999L))
                .andReturn().getResponse().getStatus();

        Assert.assertEquals(statusCode, 404, "Expected HTTP 404 for missing student");
    }

    // ── 400 Bad Request ───────────────────────────────────────────────────────
    @Test
    public void testCreateStudent_MalformedJson_Returns400() throws Exception {
        int statusCode = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andReturn().getResponse().getStatus();

        Assert.assertEquals(statusCode, 400, "Expected HTTP 400 for malformed JSON");
    }
}

