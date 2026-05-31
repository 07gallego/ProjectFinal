package com.uniremington.msstudents;

import com.uniremington.msstudents.domain.Enrollment;
import com.uniremington.msstudents.domain.Student;
import com.uniremington.msstudents.dto.EnrollmentDTO;
import com.uniremington.msstudents.dto.StudentDTO;
import com.uniremington.msstudents.exception.EnrollmentNotFoundException;
import com.uniremington.msstudents.exception.StudentNotActiveException;
import com.uniremington.msstudents.exception.StudentNotFoundException;
import com.uniremington.msstudents.repository.EnrollmentRepository;
import com.uniremington.msstudents.repository.StudentRepository;
import com.uniremington.msstudents.service.CourseClient;
import com.uniremington.msstudents.service.StudentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseClient courseClient;

    @InjectMocks
    private StudentServiceImpl studentService;

    // ── createStudent ──────────────────────────────────────────────
    @Test
    void createStudent_success() {
        StudentDTO dto = new StudentDTO();
        dto.setFirstName("Sara");
        dto.setLastName("Gallego");
        dto.setEmail("sara@test.com");

        Student saved = new Student();
        saved.setId(1L);
        saved.setFirstName("Sara");
        saved.setLastName("Gallego");
        saved.setEmail("sara@test.com");
        saved.setActive(true);

        when(studentRepository.save(any(Student.class))).thenReturn(saved);

        StudentDTO result = studentService.createStudent(dto);

        assertEquals(1L, result.getId());
        assertEquals("Sara", result.getFirstName());
        assertTrue(result.isActive());
    }

    // ── getStudentById ─────────────────────────────────────────────
    @Test
    void getStudentById_found() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Sara");
        student.setLastName("Gallego");
        student.setEmail("sara@test.com");
        student.setActive(true);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentDTO result = studentService.getStudentById(1L);
        assertEquals("Sara", result.getFirstName());
    }

    @Test
    void getStudentById_notFound_throwsException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(99L));
    }

    // ── getAllStudents ─────────────────────────────────────────────
    @Test
    void getAllStudents_returnsList() {
        Student s = new Student();
        s.setId(1L);
        s.setFirstName("Sara");
        s.setLastName("Gallego");
        s.setEmail("sara@test.com");
        s.setActive(true);

        when(studentRepository.findAll()).thenReturn(List.of(s));

        List<StudentDTO> result = studentService.getAllStudents();
        assertEquals(1, result.size());
    }

    // ── updateStudentStatus ────────────────────────────────────────
    @Test
    void updateStudentStatus_success() {
        Student student = new Student();
        student.setId(1L);
        student.setActive(true);
        student.setFirstName("Sara");
        student.setLastName("Gallego");
        student.setEmail("sara@test.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentDTO result = studentService.updateStudentStatus(1L, false);
        assertNotNull(result);
    }

    @Test
    void updateStudentStatus_notFound_throwsException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class,
                () -> studentService.updateStudentStatus(99L, false));
    }

    // ── deleteStudent ──────────────────────────────────────────────
    @Test
    void deleteStudent_success() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Sara");
        student.setLastName("Gallego");
        student.setEmail("sara@test.com");
        student.setActive(true);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).deleteById(1L);

        assertDoesNotThrow(() -> studentService.deleteStudent(1L));
        verify(studentRepository).deleteById(1L);
    }

    @Test
    void deleteStudent_notFound_throwsException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> studentService.deleteStudent(99L));
    }

    // ── enrollStudent ──────────────────────────────────────────────
    @Test
    void enrollStudent_success() {
        Student student = new Student();
        student.setId(1L);
        student.setActive(true);
        student.setFirstName("Sara");
        student.setLastName("Gallego");
        student.setEmail("sara@test.com");

        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStudentId(1L);
        enrollment.setCourseId(1L);
        enrollment.setStatus("ACTIVE");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        doNothing().when(courseClient).reserveSlot(1L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        EnrollmentDTO result = studentService.enrollStudent(1L, 1L);

        assertEquals("ACTIVE", result.getStatus());
        assertEquals(1L, result.getStudentId());
    }

    @Test
    void enrollStudent_studentNotFound_throwsException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class,
                () -> studentService.enrollStudent(99L, 1L));
    }

    @Test
    void enrollStudent_studentNotActive_throwsException() {
        Student student = new Student();
        student.setId(1L);
        student.setActive(false);
        student.setFirstName("Sara");
        student.setLastName("Gallego");
        student.setEmail("sara@test.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        assertThrows(StudentNotActiveException.class,
                () -> studentService.enrollStudent(1L, 1L));
    }

    // ── cancelEnrollment ──────────────────────────────────────────
    @Test
    void cancelEnrollment_success() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStudentId(1L);
        enrollment.setCourseId(1L);
        enrollment.setStatus("ACTIVE");

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        doNothing().when(courseClient).releaseSlot(1L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        EnrollmentDTO result = studentService.cancelEnrollment(1L);
        assertNotNull(result);
    }

    @Test
    void cancelEnrollment_notFound_throwsException() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EnrollmentNotFoundException.class,
                () -> studentService.cancelEnrollment(99L));
    }
}