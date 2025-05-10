package dev.tunalime.SemesterProject.dto;

import dev.tunalime.SemesterProject.entity.PaymentMethod;
import dev.tunalime.SemesterProject.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for Sale entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    private Long id;
    private Long customerId;
    private String customerName; // Combined first and last name for display
    private Long vehicleId;
    private String vehicleInfo; // Combined brand, model, year for display
    private LocalDate saleDate;
    private LocalDate deliveryDate;
    private BigDecimal salePrice;
    private BigDecimal discount;
    private SaleStatus status;
    private String notes;
    private String salesEmployeeName;
    private PaymentMethod paymentMethod;
} 