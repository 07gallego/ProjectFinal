package com.uniremington.msstudents.service;

import com.uniremington.msstudents.domain.Enrollment;
import com.uniremington.msstudents.domain.Student;
import com.uniremington.msstudents.dto.EnrollmentDTO;
import com.uniremington.msstudents.dto.StudentDTO;
import com.uniremington.msstudents.exception.EnrollmentNotFoundException;
import com.uniremington.msstudents.exception.StudentNotActiveException;
import com.uniremington.msstudents.exception.StudentNotFoundException;
import com.uniremington.msstudents.repository.EnrollmentRepository;
import com.uniremington.msstudents.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;

    public StudentServiceImpl(StudentRepository studentRepository,
                              EnrollmentRepository enrollmentRepository,
                              CourseClient courseClient) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseClient = courseClient;
    }

    @Override
    public StudentDTO createStudent(StudentDTO dto) {
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setActive(true);
        return toDTO(studentRepository.save(student));
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        return toDTO(student);
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO updateStudentStatus(Long id, boolean isActive) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        student.setActive(isActive);
        return toDTO(studentRepository.save(student));
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.deleteById(id);
    }

    @Override
    public EnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        // Validar que el estudiante existe y está activo
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        if (!student.isActive()) {
            throw new StudentNotActiveException(studentId);
        }

        // Llamada REST a ms-courses para reservar el cupo
        courseClient.reserveSlot(courseId);

        // Guardar la matrícula localmente
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setStatus("ACTIVE");
        return toEnrollmentDTO(enrollmentRepository.save(enrollment));
    }

    @Override
    public EnrollmentDTO cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));

        // Notificar a ms-courses para liberar el cupo
        courseClient.releaseSlot(enrollment.getCourseId());

        enrollment.setStatus("CANCELLED");
        return toEnrollmentDTO(enrollmentRepository.save(enrollment));
    }

    private StudentDTO toDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setActive(student.isActive());
        return dto;
    }

    private EnrollmentDTO toEnrollmentDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudentId());
        dto.setCourseId(enrollment.getCourseId());
        dto.setStatus(enrollment.getStatus());
        return dto;
    }
}