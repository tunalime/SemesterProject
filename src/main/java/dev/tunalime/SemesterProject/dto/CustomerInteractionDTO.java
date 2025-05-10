package dev.tunalime.SemesterProject.dto;

import dev.tunalime.SemesterProject.entity.InteractionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for CustomerInteraction entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInteractionDTO {
    private Long id;
    private Long customerId;
    private String customerName; // Combined first and last name for display
    private LocalDateTime interactionDate;
    private InteractionType type;
    private String notes;
    private Long vehicleId;
    private String vehicleInfo; // Combined brand, model, year for display
} 