package com.uniremington.mscourses.service;

import com.uniremington.mscourses.domain.Course;
import com.uniremington.mscourses.dto.CourseDTO;
import com.uniremington.mscourses.exception.CourseNotFoundException;
import com.uniremington.mscourses.exception.NoSlotsAvailableException;
import com.uniremington.mscourses.repository.CourseRepository;
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
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course buildCourse(Long id, String name, int slots) {
        Course c = new Course();
        c.setId(id);
        c.setName(name);
        c.setDescription("Descripción de " + name);
        c.setAvailableSlots(slots);
        return c;
    }

    @Test
    void saveCourse_shouldReturnSavedCourseDTO() {
        CourseDTO dto = new CourseDTO();
        dto.setName("Matemáticas");
        dto.setDescription("Cálculo I");
        dto.setAvailableSlots(30);

        Course saved = buildCourse(1L, "Matemáticas", 30);
        when(courseRepository.save(any(Course.class))).thenReturn(saved);

        CourseDTO result = courseService.saveCourse(dto);

        assertNotNull(result);
        assertEquals("Matemáticas", result.getName());
        assertEquals(30, result.getAvailableSlots());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void getCourseById_shouldReturnCourseDTO_whenCourseExists() {
        Course course = buildCourse(1L, "Física", 20);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseDTO result = courseService.getCourseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Física", result.getName());
    }

    @Test
    void getCourseById_shouldThrowCourseNotFoundException_whenCourseDoesNotExist() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class,
                () -> courseService.getCourseById(99L));
    }

    @Test
    void getAllCourses_shouldReturnListOfCourseDTOs() {
        List<Course> courses = List.of(
                buildCourse(1L, "Química", 15),
                buildCourse(2L, "Historia", 25)
        );
        when(courseRepository.findAll()).thenReturn(courses);

        List<CourseDTO> result = courseService.getAllCourses();

        assertEquals(2, result.size());
        assertEquals("Química", result.get(0).getName());
    }

    @Test
    void updateCourse_shouldReturnUpdatedCourseDTO_whenCourseExists() {
        Course existing = buildCourse(1L, "Viejo Nombre", 10);
        CourseDTO dto = new CourseDTO();
        dto.setName("Nuevo Nombre");
        dto.setDescription("Nueva desc");
        dto.setAvailableSlots(50);

        Course updated = buildCourse(1L, "Nuevo Nombre", 50);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.save(any(Course.class))).thenReturn(updated);

        CourseDTO result = courseService.updateCourse(1L, dto);

        assertEquals("Nuevo Nombre", result.getName());
        assertEquals(50, result.getAvailableSlots());
    }

    @Test
    void updateCourse_shouldThrowCourseNotFoundException_whenCourseDoesNotExist() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        CourseDTO dto = new CourseDTO();
        assertThrows(CourseNotFoundException.class,
                () -> courseService.updateCourse(99L, dto));
    }

    @Test
    void deleteCourse_shouldDeleteSuccessfully_whenCourseExists() {
        Course course = buildCourse(1L, "Arte", 10);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteCourse(1L);

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void deleteCourse_shouldThrowCourseNotFoundException_whenCourseDoesNotExist() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class,
                () -> courseService.deleteCourse(99L));
    }

    @Test
    void reserveSlot_shouldDecreaseSlotsByOne_whenSlotsAvailable() {
        Course course = buildCourse(1L, "Programación", 5);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.reserveSlot(1L);

        assertEquals(4, course.getAvailableSlots());
        verify(courseRepository).save(course);
    }

    @Test
    void reserveSlot_shouldThrowNoSlotsAvailableException_whenSlotsAreZero() {
        Course course = buildCourse(1L, "Programación", 0);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(NoSlotsAvailableException.class,
                () -> courseService.reserveSlot(1L));
        verify(courseRepository, never()).save(any());
    }

    @Test
    void reserveSlot_shouldThrowCourseNotFoundException_whenCourseDoesNotExist() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class,
                () -> courseService.reserveSlot(99L));
    }

    @Test
    void releaseSlot_shouldIncreaseSlotsByOne_whenCourseExists() {
        Course course = buildCourse(1L, "Biología", 3);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.releaseSlot(1L);

        assertEquals(4, course.getAvailableSlots());
        verify(courseRepository).save(course);
    }

    @Test
    void releaseSlot_shouldThrowCourseNotFoundException_whenCourseDoesNotExist() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class,
                () -> courseService.releaseSlot(99L));
    }
}