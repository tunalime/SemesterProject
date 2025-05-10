package dev.tunalime.SemesterProject.repository;

import dev.tunalime.SemesterProject.entity.Sale;
import dev.tunalime.SemesterProject.entity.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    List<Sale> findByCustomerId(Long customerId);
    
    List<Sale> findByStatus(SaleStatus status);
    
    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM Sale s WHERE s.vehicle.brand = ?1")
    List<Sale> findByVehicleBrand(String brand);
    
    @Query("SELECT s FROM Sale s WHERE s.vehicle.brand = ?1 AND s.vehicle.model = ?2")
    List<Sale> findByVehicleBrandAndModel(String brand, String model);
    
    @Query("SELECT s FROM Sale s WHERE s.vehicle.brand = ?1 AND s.vehicle.model = ?2 AND s.vehicle.year = ?3")
    List<Sale> findByVehicleBrandAndModelAndYear(String brand, String model, Integer year);
    
    @Query("SELECT s FROM Sale s WHERE s.vehicle.brand = ?1 AND s.vehicle.model = ?2 AND s.vehicle.year = ?3 AND s.vehicle.packageType = ?4")
    List<Sale> findByVehicleBrandAndModelAndYearAndPackage(String brand, String model, Integer year, String packageType);
    
    // Count sales per brand
    @Query("SELECT s.vehicle.brand, COUNT(s) FROM Sale s GROUP BY s.vehicle.brand")
    List<Object[]> countSalesByBrand();
    
    // Count sales per model for a given brand
    @Query("SELECT s.vehicle.model, COUNT(s) FROM Sale s WHERE s.vehicle.brand = ?1 GROUP BY s.vehicle.model")
    List<Object[]> countSalesByModelForBrand(String brand);
    
    // Count sales per year model for a given brand and model
    @Query("SELECT s.vehicle.year, COUNT(s) FROM Sale s WHERE s.vehicle.brand = ?1 AND s.vehicle.model = ?2 GROUP BY s.vehicle.year")
    List<Object[]> countSalesByYearForBrandAndModel(String brand, String model);
    
    // Count sales per package for a given brand, model, and year
    @Query("SELECT s.vehicle.packageType, COUNT(s) FROM Sale s WHERE s.vehicle.brand = ?1 AND s.vehicle.model = ?2 AND s.vehicle.year = ?3 GROUP BY s.vehicle.packageType")
    List<Object[]> countSalesByPackageForBrandAndModelAndYear(String brand, String model, Integer year);
    
    // Count total sales for a given brand, model, year, and package
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.vehicle.brand = ?1 AND s.vehicle.model = ?2 AND s.vehicle.year = ?3 AND s.vehicle.packageType = ?4")
    Long countSalesByBrandAndModelAndYearAndPackage(String brand, String model, Integer year, String packageType);
    
    // Count sales per month for a specific year
    @Query("SELECT FUNCTION('MONTH', s.saleDate) as month, COUNT(s) FROM Sale s WHERE FUNCTION('YEAR', s.saleDate) = ?1 GROUP BY FUNCTION('MONTH', s.saleDate) ORDER BY month")
    List<Object[]> countSalesByMonthForYear(Integer year);
    
    // Get sales data for moving average calculation
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.vehicle.brand = ?1 AND s.vehicle.model = ?2 AND s.vehicle.packageType = ?3 AND s.saleDate BETWEEN ?4 AND ?5")
    Long countSalesForPeriod(String brand, String model, String packageType, LocalDate startDate, LocalDate endDate);
} 