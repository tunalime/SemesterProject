package dev.tunalime.SemesterProject.entity;

/**
 * Enum representing the status of a vehicle in the system
 */
public enum VehicleStatus {
    IN_STOCK,         // Vehicle is in stock, not yet moved to showroom
    IN_SHOWROOM,      // Vehicle is in the showroom for display
    RESERVED,         // Vehicle has been reserved for a customer
    SOLD,             // Vehicle has been sold
    DELIVERED,        // Vehicle has been delivered to the customer
    UNDER_MAINTENANCE // Vehicle is under maintenance
} 