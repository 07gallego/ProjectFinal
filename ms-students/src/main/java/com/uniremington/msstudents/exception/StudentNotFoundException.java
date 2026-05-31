package com.uniremington.msstudents.exception;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Long studentId) {
        super("Estudiante no encontrado con ID: " + studentId);
    }
}