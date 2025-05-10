package dev.tunalime.SemesterProject.entity;

/**
 * Enum representing the types of interactions with customers
 */
public enum InteractionType {
    INQUIRY,            // Customer asked for information
    VISIT,              // Customer visited the showroom
    TEST_DRIVE_REQUEST, // Customer requested a test drive
    QUOTE_REQUEST,      // Customer requested a price quote
    PRICE_NEGOTIATION,  // Customer negotiated on price
    FOLLOW_UP,          // Follow-up contact with customer
    COMPLAINT,          // Customer filed a complaint
    PURCHASE,           // Customer purchased a vehicle
    OTHER               // Other types of interaction
} 