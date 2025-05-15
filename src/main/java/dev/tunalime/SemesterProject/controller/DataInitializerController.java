package dev.tunalime.SemesterProject.controller;

import dev.tunalime.SemesterProject.config.SampleDataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for sample data initialization
 */
@RestController
public class DataInitializerController {

    @Autowired
    private SampleDataInitializer sampleDataInitializer;

    /**
     * Endpoint to initialize sample data for development and testing
     * @return Summary of data created
     */
    @GetMapping("/createExamples")
    public ResponseEntity<Map<String, Integer>> createExamples() {
        Map<String, Integer> result = sampleDataInitializer.initializeData();
        return ResponseEntity.ok(result);
    }
} 