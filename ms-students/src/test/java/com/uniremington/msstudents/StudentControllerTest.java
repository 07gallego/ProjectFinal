package com.uniremington.msstudents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniremington.msstudents.controller.StudentController;
import com.uniremington.msstudents.dto.EnrollmentDTO;
import com.uniremington.msstudents.dto.StudentDTO;
import com.uniremington.msstudents.exception.EnrollmentNotFoundException;
import com.uniremington.msstudents.exception.StudentNotActiveException;
import com.uniremington.msstudents.exception.StudentNotFoundException;
import com.uniremington.msstudents.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    // ── POST /api/students ─────────────────────────────────────────
    @Test
    void createStudent_returns201() throws Exception {
        StudentDTO dto = new StudentDTO();
        dto.setFirstName("Sara");
        dto.setLastName("Gallego");
        dto.setEmail("sara@test.com");
        dto.setActive(true);

        StudentDTO response = new StudentDTO();
        response.setId(1L);
        response.setFirstName("Sara");
        response.setLastName("Gallego");
        response.setEmail("sara@test.com");
        response.setActive(true);

        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Sara"));
    }

    // ── GET /api/students/{id} ─────────────────────────────────────
    @Test
    void getStudentById_returns200() throws Exception {
        StudentDTO response = new StudentDTO();
        response.setId(1L);
        response.setFirstName("Sara");
        response.setLastName("Gallego");
        response.setEmail("sara@test.com");
        response.setActive(true);

        when(studentService.getStudentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Sara"));
    }

    @Test
    void getStudentById_notFound_returns404() throws Exception {
        when(studentService.getStudentById(99L))
                .thenThrow(new StudentNotFoundException(99L));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/students ──────────────────────────────────────────
    @Test
    void getAllStudents_returns200() throws Exception {
        StudentDTO dto = new StudentDTO();
        dto.setId(1L);
        dto.setFirstName("Sara");
        dto.setLastName("Gallego");
        dto.setEmail("sara@test.com");
        dto.setActive(true);

        when(studentService.getAllStudents()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Sara"));
    }

    // ── PATCH /api/students/{id}/status ───────────────────────────
    @Test
    void updateStudentStatus_returns200() throws Exception {
        StudentDTO response = new StudentDTO();
        response.setId(1L);
        response.setFirstName("Sara");
        response.setLastName("Gallego");
        response.setEmail("sara@test.com");
        response.setActive(false);

        when(studentService.updateStudentStatus(1L, false)).thenReturn(response);

        mockMvc.perform(patch("/api/students/1/status")
                        .param("isActive", "false"))
                .andExpect(status().isOk());
    }

    // ── DELETE /api/students/{id} ──────────────────────────────────
    @Test
    void deleteStudent_returns204() throws Exception {
        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());
    }

    // ── POST /api/students/enroll ──────────────────────────────────
    @Test
    void enrollStudent_returns201() throws Exception {
        EnrollmentDTO response = new EnrollmentDTO();
        response.setId(1L);
        response.setStudentId(1L);
        response.setCourseId(1L);
        response.setStatus("ACTIVE");

        when(studentService.enrollStudent(1L, 1L)).thenReturn(response);

        mockMvc.perform(post("/api/students/enroll")
                        .param("studentId", "1")
                        .param("courseId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void enrollStudent_studentNotActive_returns409() throws Exception {
        when(studentService.enrollStudent(anyLong(), anyLong()))
                .thenThrow(new StudentNotActiveException(1L));

        mockMvc.perform(post("/api/students/enroll")
                        .param("studentId", "1")
                        .param("courseId", "1"))
                .andExpect(status().isConflict());
    }

    // ── PUT /api/students/enrollments/{id}/cancel ──────────────────
    @Test
    void cancelEnrollment_returns200() throws Exception {
        EnrollmentDTO response = new EnrollmentDTO();
        response.setId(1L);
        response.setStudentId(1L);
        response.setCourseId(1L);
        response.setStatus("CANCELLED");

        when(studentService.cancelEnrollment(1L)).thenReturn(response);

        mockMvc.perform(put("/api/students/enrollments/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelEnrollment_notFound_returns404() throws Exception {
        when(studentService.cancelEnrollment(99L))
                .thenThrow(new EnrollmentNotFoundException(99L));

        mockMvc.perform(put("/api/students/enrollments/99/cancel"))
                .andExpect(status().isNotFound());
    }
}