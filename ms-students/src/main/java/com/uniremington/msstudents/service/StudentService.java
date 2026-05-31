package com.uniremington.msstudents.service;

import com.uniremington.msstudents.dto.EnrollmentDTO;
import com.uniremington.msstudents.dto.StudentDTO;

import java.util.List;

public interface StudentService {

    StudentDTO createStudent(StudentDTO dto);
    StudentDTO getStudentById(Long id);
    List<StudentDTO> getAllStudents();
    StudentDTO updateStudentStatus(Long id, boolean isActive);
    void deleteStudent(Long id);
    EnrollmentDTO enrollStudent(Long studentId, Long courseId);
    EnrollmentDTO cancelEnrollment(Long enrollmentId);
}