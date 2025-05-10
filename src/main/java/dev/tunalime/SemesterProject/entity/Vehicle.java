package dev.tunalime.SemesterProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a vehicle in the system
 */
@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String brand;
    
    @Column(nullable = false)
    private String model;
    
    @Column(name = "model_year", nullable = false)
    private Integer year;
    
    @Column(nullable = false)
    private String packageType;
    
    @Column(nullable = false)
    private String vin; // Vehicle Identification Number
    
    @Column(nullable = false)
    private BigDecimal price;
    
    private String color;
    
    @Column(nullable = false)
    private LocalDate stockEntryDate;
    
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private boolean isAvailableForTestDrive;
    
    // Many vehicles can be of the same stock item type (same model, package, etc.)
    @ManyToOne
    @JoinColumn(name = "stock_item_id")
    private StockItem stockItem;
} 