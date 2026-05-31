package com.uniremington.mscourses.controller;

import com.uniremington.mscourses.dto.CourseDTO;
import com.uniremington.mscourses.exception.CourseNotFoundException;
import com.uniremington.mscourses.exception.NoSlotsAvailableException;
import com.uniremington.mscourses.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<CourseDTO> saveCourse(@RequestBody CourseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.saveCourse(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable("id") Long id, @RequestBody CourseDTO dto) {
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/reserve")
    public ResponseEntity<Void> reserveSlot(@PathVariable("courseId") Long courseId) {
        courseService.reserveSlot(courseId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseId}/release")
    public ResponseEntity<Void> releaseSlot(@PathVariable("courseId") Long courseId) {
        courseService.releaseSlot(courseId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<String> handleNotFound(CourseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NoSlotsAvailableException.class)
    public ResponseEntity<String> handleNoSlots(NoSlotsAvailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}