package tests;

import com.sliit.se3010_testing.model.Student;
import com.sliit.se3010_testing.repository.StudentRepository;
import com.sliit.se3010_testing.service.StudentService;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Member 2 – TestNG Fixtures (@BeforeMethod, @AfterMethod, @DataProvider)
 * Uses Mockito standalone – no JUnit, no Selenium.
 */
public class StudentServiceTest {

    private StudentRepository studentRepository;
    private StudentService studentService;

    // ── Fixture: fresh mocks before every test / DataProvider row ────────────
    @BeforeMethod
    public void setUp() {
        studentRepository = mock(StudentRepository.class);
        studentService    = new StudentService(studentRepository);
        System.out.println("[FIXTURE] Fresh mocks created – test starting");
    }

    // ── Fixture: verify no unexpected interactions after each test ────────────
    @AfterMethod
    public void tearDown() {
        verifyNoMoreInteractions(studentRepository);
        System.out.println("[FIXTURE] Verified – test finished");
    }

    // ── DataProvider: multiple student records ────────────────────────────────
    @DataProvider(name = "studentData")
    public Object[][] studentDataProvider() {
        return new Object[][] {
            {"Alice", "alice@test.com", 22, "SE3010"},
            {"Bob",   "bob@test.com",   25, "SE3020"},
            {"Carol", "carol@test.com", 20, "SE3030"},
        };
    }

    // ── Test: createStudent saved correctly (parameterised) ──────────────────
    @Test(dataProvider = "studentData")
    public void testCreateStudent(String name, String email, int age, String course) {
        Student input = Student.builder().name(name).email(email).age(age).course(course).build();
        Student saved = Student.builder().id(1L).name(name).email(email).age(age).course(course).build();
        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(saved);

        Student result = studentService.createStudent(input);

        Assert.assertNotNull(result.getId(), "Saved student must have an ID");
        Assert.assertEquals(result.getName(), name, "Name should match");
        Assert.assertEquals(result.getEmail(), email, "Email should match");
        verify(studentRepository).findByEmail(email);
        verify(studentRepository).save(any(Student.class));
    }

    // ── Test: getStudentById – found ──────────────────────────────────────────
    @Test
    public void testGetStudentById_Found() {
        Student student = Student.builder().id(1L).name("Dave").email("dave@test.com").age(21).course("SE3040").build();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Optional<Student> result = studentService.getStudentById(1L);

        Assert.assertTrue(result.isPresent(), "Student should be present");
        Assert.assertEquals(result.get().getName(), "Dave");
        verify(studentRepository).findById(1L);
    }

    // ── Test: getStudentById – not found ─────────────────────────────────────
    @Test
    public void testGetStudentById_NotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Student> result = studentService.getStudentById(99L);

        Assert.assertFalse(result.isPresent(), "Student should not be present");
        verify(studentRepository).findById(99L);
    }

    // ── Test: getAllStudents ──────────────────────────────────────────────────
    @Test
    public void testGetAllStudents_ReturnsList() {
        List<Student> list = List.of(
            Student.builder().id(1L).name("Eve").email("eve@test.com").age(23).course("SE3050").build()
        );
        when(studentRepository.findAll()).thenReturn(list);

        List<Student> result = studentService.getAllStudents();

        Assert.assertEquals(result.size(), 1, "Should return exactly one student");
        Assert.assertEquals(result.get(0).getName(), "Eve");
        verify(studentRepository).findAll();
    }
}
