package dev.tunalime.SemesterProject.dto;

import dev.tunalime.SemesterProject.entity.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for Vehicle entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String packageType;
    private String vin;
    private BigDecimal price;
    private String color;
    private Integer mileage;
    private LocalDate stockEntryDate;
    private VehicleStatus status;
    private String description;
    private boolean isAvailableForTestDrive;
    private Long stockItemId;
} 