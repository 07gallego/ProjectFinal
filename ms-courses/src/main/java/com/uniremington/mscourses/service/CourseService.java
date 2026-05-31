package com.uniremington.mscourses.service;

import com.uniremington.mscourses.dto.CourseDTO;
import java.util.List;

public interface CourseService {

    CourseDTO saveCourse(CourseDTO dto);
    CourseDTO getCourseById(Long id);
    List<CourseDTO> getAllCourses();
    CourseDTO updateCourse(Long id, CourseDTO dto);
    void deleteCourse(Long id);
    void reserveSlot(Long courseId);
    void releaseSlot(Long courseId);
}