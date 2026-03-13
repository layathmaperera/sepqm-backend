package com.sliit.se3010_testing.service;

import com.sliit.se3010_testing.model.Student;
import com.sliit.se3010_testing.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String email) {
            super("A student with email '" + email + "' already exists.");
        }
    }

    public Student createStudent(Student student) {
        studentRepository.findByEmail(student.getEmail()).ifPresent(existing -> {
            throw new DuplicateEmailException(student.getEmail());
        });
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student updateStudent(Long id, Student studentDetails) {
        return studentRepository.findById(id).map(student -> {
            student.setName(studentDetails.getName());
            student.setEmail(studentDetails.getEmail());
            student.setAge(studentDetails.getAge());
            student.setCourse(studentDetails.getCourse());
            return studentRepository.save(student);
        }).orElseThrow(() -> new RuntimeException("Student not found with id " + id));
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
