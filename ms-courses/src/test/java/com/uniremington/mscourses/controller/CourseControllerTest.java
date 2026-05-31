package com.uniremington.mscourses.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniremington.mscourses.dto.CourseDTO;
import com.uniremington.mscourses.exception.CourseNotFoundException;
import com.uniremington.mscourses.exception.NoSlotsAvailableException;
import com.uniremington.mscourses.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    private CourseDTO buildDTO(Long id, String name, int slots) {
        CourseDTO dto = new CourseDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription("Descripción de " + name);
        dto.setAvailableSlots(slots);
        return dto;
    }

    @Test
    void createCourse_shouldReturn201_whenValidDTO() throws Exception {
        CourseDTO input = buildDTO(null, "Matemáticas", 30);
        CourseDTO saved = buildDTO(1L, "Matemáticas", 30);

        when(courseService.saveCourse(any(CourseDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Matemáticas"));
    }

    @Test
    void getCourseById_shouldReturn200_whenCourseExists() throws Exception {
        CourseDTO dto = buildDTO(1L, "Física", 20);
        when(courseService.getCourseById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Física"));
    }

    @Test
    void getCourseById_shouldReturn404_whenCourseNotFound() throws Exception {
        when(courseService.getCourseById(99L))
                .thenThrow(new CourseNotFoundException("Curso no encontrado"));

        mockMvc.perform(get("/api/courses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCourses_shouldReturn200_withList() throws Exception {
        List<CourseDTO> list = List.of(
                buildDTO(1L, "Química", 15),
                buildDTO(2L, "Historia", 25)
        );
        when(courseService.getAllCourses()).thenReturn(list);

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Química"));
    }

    @Test
    void updateCourse_shouldReturn200_whenCourseExists() throws Exception {
        CourseDTO input = buildDTO(null, "Nuevo Nombre", 50);
        CourseDTO updated = buildDTO(1L, "Nuevo Nombre", 50);

        when(courseService.updateCourse(eq(1L), any(CourseDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nuevo Nombre"))
                .andExpect(jsonPath("$.availableSlots").value(50));
    }

    @Test
    void updateCourse_shouldReturn404_whenCourseNotFound() throws Exception {
        when(courseService.updateCourse(eq(99L), any(CourseDTO.class)))
                .thenThrow(new CourseNotFoundException("Curso no encontrado"));

        mockMvc.perform(put("/api/courses/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO(null, "X", 10))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourse_shouldReturn204_whenCourseExists() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCourse_shouldReturn404_whenCourseNotFound() throws Exception {
        doThrow(new CourseNotFoundException("Curso no encontrado"))
                .when(courseService).deleteCourse(99L);

        mockMvc.perform(delete("/api/courses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reserveSlot_shouldReturn200_whenSlotsAvailable() throws Exception {
        doNothing().when(courseService).reserveSlot(1L);

        mockMvc.perform(post("/api/courses/1/reserve"))
                .andExpect(status().isOk());
    }

    @Test
    void reserveSlot_shouldReturn409_whenNoSlotsAvailable() throws Exception {
        doThrow(new NoSlotsAvailableException("Sin cupos"))
                .when(courseService).reserveSlot(1L);

        mockMvc.perform(post("/api/courses/1/reserve"))
                .andExpect(status().isConflict());
    }

    @Test
    void releaseSlot_shouldReturn200_whenCourseExists() throws Exception {
        doNothing().when(courseService).releaseSlot(1L);

        mockMvc.perform(post("/api/courses/1/release"))
                .andExpect(status().isOk());
    }

    @Test
    void releaseSlot_shouldReturn404_whenCourseNotFound() throws Exception {
        doThrow(new CourseNotFoundException("Curso no encontrado"))
                .when(courseService).releaseSlot(99L);

        mockMvc.perform(post("/api/courses/99/release"))
                .andExpect(status().isNotFound());
    }
}