package dev.tunalime.SemesterProject.entity;

/**
 * Enum representing the status of a customer in the system
 */
public enum CustomerStatus {
    LEAD,           // Potential customer who has shown interest
    PROSPECT,       // Customer who has been contacted and might purchase
    ACTIVE,         // Customer who has purchased at least one vehicle
    REPEAT,         // Customer who has purchased multiple vehicles
    INACTIVE        // Customer who hasn't purchased or interacted for a long time
} 