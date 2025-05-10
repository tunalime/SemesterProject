package dev.tunalime.SemesterProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for StockItem entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItemDTO {
    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String packageType;
    private BigDecimal basePrice;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private String specifications;
} 