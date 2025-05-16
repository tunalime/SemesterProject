package dev.tunalime.SemesterProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity representing a sales forecast
 */
@Entity
@Table(name = "sales_forecasts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesForecast {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // The vehicle model and package being forecasted
    private String brand;
    private String model;
    private String packageType;
    
    @Column(name = "model_year")
    private Integer year;
    
    // The period for which the forecast is made
    @Column(nullable = false)
    private LocalDate forecastPeriodStart;
    
    @Column(nullable = false)
    private LocalDate forecastPeriodEnd;
    
    // The forecast quantity
    @Column(nullable = false)
    private Integer forecastedQuantity;
    
    // The actual quantity (to be filled after the period)
    private Integer actualQuantity;
    
    // The number of periods used for the moving average calculation
    @Column(nullable = false)
    private Integer numberOfPeriods;
    
    // The creation date of the forecast
    @Column(nullable = false)
    private LocalDate creationDate;
} 