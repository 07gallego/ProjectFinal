package com.uniremington.mscourses.exception;

public class NoSlotsAvailableException extends RuntimeException {

    public NoSlotsAvailableException(Long courseId) {
        super("No hay cupos disponibles para el curso con ID: " + courseId);
    }

    public NoSlotsAvailableException(String message) {
        super(message);
    }
}