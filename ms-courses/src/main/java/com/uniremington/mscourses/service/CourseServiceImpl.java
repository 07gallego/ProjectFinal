package com.uniremington.mscourses.service;

import com.uniremington.mscourses.domain.Course;
import com.uniremington.mscourses.dto.CourseDTO;
import com.uniremington.mscourses.exception.CourseNotFoundException;
import com.uniremington.mscourses.exception.NoSlotsAvailableException;
import com.uniremington.mscourses.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseDTO saveCourse(CourseDTO dto) {
        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setAvailableSlots(dto.getAvailableSlots());
        Course saved = courseRepository.save(course);
        return toDTO(saved);
    }

    @Override
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        return toDTO(course);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDTO updateCourse(Long id, CourseDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setAvailableSlots(dto.getAvailableSlots());
        return toDTO(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        courseRepository.deleteById(id);
    }

    @Override
    public void reserveSlot(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        if (course.getAvailableSlots() <= 0) {
            throw new NoSlotsAvailableException(courseId);
        }
        course.setAvailableSlots(course.getAvailableSlots() - 1);
        courseRepository.save(course);
    }

    @Override
    public void releaseSlot(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        course.setAvailableSlots(course.getAvailableSlots() + 1);
        courseRepository.save(course);
    }

    // Método privado para convertir Course → CourseDTO
    private CourseDTO toDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setAvailableSlots(course.getAvailableSlots());
        return dto;
    }
}