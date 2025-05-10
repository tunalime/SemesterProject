package dev.tunalime.SemesterProject.entity;

/**
 * Enum representing the status of a sale
 */
public enum SaleStatus {
    INITIATED,     // Sale process has been initiated
    PENDING,       // Sale is pending (e.g., waiting for payment)
    COMPLETED,     // Sale has been completed
    CANCELLED,     // Sale was cancelled
    DELIVERED      // Vehicle has been delivered to the customer
} 