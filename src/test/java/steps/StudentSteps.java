package steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.se3010_testing.Se3010TestingApplication;
import com.sliit.se3010_testing.model.Student;
import com.sliit.se3010_testing.repository.StudentRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.Assert;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Member 3 – Cucumber BDD step definitions using MockMvc (no Selenium, no browser).
 */
@CucumberContextConfiguration
@SpringBootTest(classes = Se3010TestingApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult lastResult;

    // ── Background hooks ────────────────────────────────────────────────────
    @Before
    public void resetDatabase() {
        studentRepository.deleteAll();
    }

    // ── Given ───────────────────────────────────────────────────────────────
    @Given("no students exist in the system")
    public void no_students_exist() {
        studentRepository.deleteAll();
    }

    @Given("a student with email {string} already exists")
    public void a_student_with_email_already_exists(String email) {
        studentRepository.deleteAll();
        studentRepository.save(
                Student.builder().name("Existing").email(email).age(20).course("SE0000").build());
    }

    // ── When ────────────────────────────────────────────────────────────────
    @When("I register a student with name {string} email {string} age {int} course {string}")
    public void i_register_a_student(String name, String email, int age, String course) throws Exception {
        Student s = Student.builder().name(name).email(email).age(age).course(course).build();
        lastResult = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(s)))
                .andReturn();
    }

    @When("I send a registration request with malformed JSON body")
    public void i_send_malformed_json() throws Exception {
        lastResult = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ bad json }"))
                .andReturn();
    }

    // ── Then ────────────────────────────────────────────────────────────────
    @Then("the registration response status should be {int}")
    public void the_registration_status_should_be(int expectedStatus) {
        int actual = lastResult.getResponse().getStatus();
        Assert.assertEquals(actual, expectedStatus,
                "Expected HTTP " + expectedStatus + " but got " + actual);
    }

    @Then("the registered student name should be {string}")
    public void the_registered_student_name_should_be(String expectedName) throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        Student returned = objectMapper.readValue(body, Student.class);
        Assert.assertEquals(returned.getName(), expectedName, "Student name mismatch");
    }
}

