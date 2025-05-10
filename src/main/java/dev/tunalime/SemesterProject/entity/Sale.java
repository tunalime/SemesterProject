package dev.tunalime.SemesterProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a vehicle sale
 */
@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @OneToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Column(nullable = false)
    private LocalDate saleDate;
    
    @Column(nullable = false)
    private BigDecimal salePrice;
    
    private BigDecimal discount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status;
    
    // Sales employee who made the sale
    private String salesEmployeeName;
    
    // Payment method used for the sale
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
} 