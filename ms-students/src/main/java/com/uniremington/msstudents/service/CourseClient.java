package com.uniremington.msstudents.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CourseClient {

    private final RestTemplate restTemplate;
    private static final String COURSES_URL = "http://localhost:8081/api/courses";

    public CourseClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void reserveSlot(Long courseId) {
        restTemplate.postForEntity(COURSES_URL + "/" + courseId + "/reserve", null, Void.class);
    }

    public void releaseSlot(Long courseId) {
        restTemplate.postForEntity(COURSES_URL + "/" + courseId + "/release", null, Void.class);
    }
}