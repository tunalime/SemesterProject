package dev.tunalime.SemesterProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for SalesForecast entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesForecastDTO {
    private Long id;
    private String brand;
    private String model;
    private String packageType;
    private Integer year;
    private LocalDate forecastPeriodStart;
    private LocalDate forecastPeriodEnd;
    private Integer forecastedQuantity;
    private Integer actualQuantity;
    private Integer numberOfPeriods;
    private LocalDate creationDate;
    
    // Calculate accuracy if both forecasted and actual quantities are available
    public Double getAccuracy() {
        if (forecastedQuantity != null && actualQuantity != null && forecastedQuantity > 0) {
            double deviation = Math.abs(actualQuantity - forecastedQuantity);
            return 100.0 - (deviation / forecastedQuantity * 100.0);
        }
        return null;
    }
} 