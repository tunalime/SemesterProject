package dev.tunalime.SemesterProject.repository;

import dev.tunalime.SemesterProject.entity.SalesForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesForecastRepository extends JpaRepository<SalesForecast, Long> {
    
    List<SalesForecast> findByBrandAndModel(String brand, String model);
    
    List<SalesForecast> findByBrandAndModelAndPackageType(String brand, String model, String packageType);
    
    List<SalesForecast> findByBrandAndModelAndPackageTypeAndYear(String brand, String model, String packageType, Integer year);
    
    List<SalesForecast> findByForecastPeriodStartBetween(LocalDate startDate, LocalDate endDate);
    
    // Find the most recent forecast for a specific vehicle configuration
    @Query("SELECT f FROM SalesForecast f WHERE f.brand = ?1 AND f.model = ?2 AND f.packageType = ?3 AND f.year = ?4 ORDER BY f.creationDate DESC")
    List<SalesForecast> findMostRecentForecastForVehicle(String brand, String model, String packageType, Integer year);
    
    // Find forecasts where actual quantity is significantly different from forecasted quantity (for analysis)
    @Query("SELECT f FROM SalesForecast f WHERE f.actualQuantity IS NOT NULL AND ABS(f.actualQuantity - f.forecastedQuantity) / f.forecastedQuantity > 0.2")
    List<SalesForecast> findForecastsWithSignificantDeviation();
    
    // Calculate average accuracy of forecasts for a specific vehicle type
    @Query("SELECT AVG(ABS(f.actualQuantity - f.forecastedQuantity) / f.forecastedQuantity) FROM SalesForecast f WHERE f.actualQuantity IS NOT NULL AND f.brand = ?1 AND f.model = ?2 AND f.packageType = ?3")
    Double calculateAverageForecastAccuracy(String brand, String model, String packageType);
} 