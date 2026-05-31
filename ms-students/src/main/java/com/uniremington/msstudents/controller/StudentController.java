package com.uniremington.msstudents.controller;

import com.uniremington.msstudents.dto.EnrollmentDTO;
import com.uniremington.msstudents.dto.StudentDTO;
import com.uniremington.msstudents.exception.EnrollmentNotFoundException;
import com.uniremington.msstudents.exception.StudentNotActiveException;
import com.uniremington.msstudents.exception.StudentNotFoundException;
import com.uniremington.msstudents.service.StudentService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<StudentDTO> updateStudentStatus(@PathVariable Long id,
                                                          @RequestParam boolean isActive) {
        return ResponseEntity.ok(studentService.updateStudentStatus(id, isActive));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentDTO> enrollStudent(
            @Parameter(name = "studentId") @RequestParam Long studentId,
            @Parameter(name = "courseId") @RequestParam Long courseId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.enrollStudent(studentId, courseId));
    }

    @PutMapping("/enrollments/{enrollmentId}/cancel")
    public ResponseEntity<EnrollmentDTO> cancelEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(studentService.cancelEnrollment(enrollmentId));
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<String> handleStudentNotFound(StudentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(StudentNotActiveException.class)
    public ResponseEntity<String> handleStudentNotActive(StudentNotActiveException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<String> handleEnrollmentNotFound(EnrollmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}