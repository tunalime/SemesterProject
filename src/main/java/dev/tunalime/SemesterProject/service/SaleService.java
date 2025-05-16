package dev.tunalime.SemesterProject.service;

import dev.tunalime.SemesterProject.entity.*;
import dev.tunalime.SemesterProject.repository.CustomerRepository;
import dev.tunalime.SemesterProject.repository.SaleRepository;
import dev.tunalime.SemesterProject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {
    
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    
    @Autowired
    public SaleService(SaleRepository saleRepository, 
                       CustomerRepository customerRepository,
                       VehicleRepository vehicleRepository) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
    }
    
    /**
     * Get all sales
     */
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
    
    /**
     * Get sale by id
     */
    public Optional<Sale> getSaleById(Long id) {
        return saleRepository.findById(id);
    }
    
    /**
     * Get sales by customer id
     */
    public List<Sale> getSalesByCustomerId(Long customerId) {
        return saleRepository.findByCustomerId(customerId);
    }
    
    /**
     * Initialize a new sale
     */
    @Transactional
    public Sale initiateSale(Long customerId, Long vehicleId, BigDecimal salePrice, 
                            BigDecimal discount, PaymentMethod paymentMethod) {
        
        // Get customer and vehicle
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
            
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
            
        // Validate vehicle availability
        if (vehicle.getStatus() != VehicleStatus.IN_STOCK && 
            vehicle.getStatus() != VehicleStatus.IN_SHOWROOM) {
            throw new IllegalStateException("Vehicle is not available for sale");
        }
        
        // Create new sale
        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setVehicle(vehicle);
        sale.setSaleDate(LocalDate.now());
        sale.setSalePrice(salePrice);
        sale.setDiscount(discount);
        sale.setStatus(SaleStatus.INITIATED);
        sale.setPaymentMethod(paymentMethod);
        
        // Update vehicle status
        vehicle.setStatus(VehicleStatus.RESERVED);
        vehicleRepository.save(vehicle);
        
        // Save and return the sale
        return saleRepository.save(sale);
    }
    
    /**
     * Complete a sale
     */
    @Transactional
    public Sale completeSale(Long saleId, String salesEmployeeName) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new IllegalArgumentException("Sale not found"));
            
        // Check if sale is in appropriate status
        if (sale.getStatus() != SaleStatus.INITIATED && sale.getStatus() != SaleStatus.PENDING) {
            throw new IllegalStateException("Sale cannot be completed from current status: " + sale.getStatus());
        }
        
        // Update sale
        sale.setStatus(SaleStatus.COMPLETED);
        sale.setSalesEmployeeName(salesEmployeeName);
        
        // Update vehicle
        Vehicle vehicle = sale.getVehicle();
        vehicle.setStatus(VehicleStatus.SOLD);
        vehicleRepository.save(vehicle);
        
        return saleRepository.save(sale);
    }
    
    /**
     * Cancel a sale
     */
    @Transactional
    public Sale cancelSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new IllegalArgumentException("Sale not found"));
            
        // Check if sale can be cancelled
        if (sale.getStatus() == SaleStatus.COMPLETED || sale.getStatus() == SaleStatus.DELIVERED) {
            throw new IllegalStateException("Completed or delivered sales cannot be cancelled");
        }
        
        // Update sale
        sale.setStatus(SaleStatus.CANCELLED);
        
        // Update vehicle status
        Vehicle vehicle = sale.getVehicle();
        vehicle.setStatus(VehicleStatus.IN_STOCK);
        vehicleRepository.save(vehicle);
        
        return saleRepository.save(sale);
    }
    
    /**
     * Mark a sale as delivered
     */
    @Transactional
    public Sale markAsDelivered(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new IllegalArgumentException("Sale not found"));
            
        // Check if sale is completed
        if (sale.getStatus() != SaleStatus.COMPLETED) {
            throw new IllegalStateException("Sale must be completed before delivery");
        }
        
        // Update sale
        sale.setStatus(SaleStatus.DELIVERED);
        
        return saleRepository.save(sale);
    }
    
    /**
     * Update sale payment method
     */
    @Transactional
    public Sale updatePaymentMethod(Long saleId, PaymentMethod paymentMethod) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new IllegalArgumentException("Sale not found"));
        
        sale.setPaymentMethod(paymentMethod);
        return saleRepository.save(sale);
    }
    
    /**
     * Get sales by status
     */
    public List<Sale> getSalesByStatus(SaleStatus status) {
        return saleRepository.findByStatus(status);
    }
    
    /**
     * Get sales by date range
     */
    public List<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate);
    }
} 