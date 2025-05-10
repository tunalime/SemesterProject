package dev.tunalime.SemesterProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing an interaction with a customer
 */
@Entity
@Table(name = "customer_interactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInteraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false)
    private LocalDateTime interactionDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType type;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // If the interaction is related to a specific vehicle
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
} 