package com.uniremington.mscourses.exception;

public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(Long courseId) {
        super("Curso no encontrado con ID: " + courseId);
    }

    public CourseNotFoundException(String message) {
        super(message);
    }
}