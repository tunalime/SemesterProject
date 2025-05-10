package dev.tunalime.SemesterProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a stock item (vehicle type) in the inventory
 */
@Entity
@Table(name = "stock_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItem {
    
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
    private BigDecimal basePrice;
    
    @Column(nullable = false)
    private Integer totalQuantity;
    
    @Column(nullable = false)
    private Integer availableQuantity;
    
    @Column(columnDefinition = "TEXT")
    private String specifications;
    
    // One stock item (model type) can have many physical vehicles
    @OneToMany(mappedBy = "stockItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();
} 