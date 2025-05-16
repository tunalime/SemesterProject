package dev.tunalime.SemesterProject.service;

import dev.tunalime.SemesterProject.entity.SalesForecast;
import dev.tunalime.SemesterProject.repository.SaleRepository;
import dev.tunalime.SemesterProject.repository.SalesForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for forecasting sales using Moving Average method
 */
@Service
public class ForecastService {
    
    private final SaleRepository saleRepository;
    private final SalesForecastRepository salesForecastRepository;
    
    @Autowired
    public ForecastService(SaleRepository saleRepository, SalesForecastRepository salesForecastRepository) {
        this.saleRepository = saleRepository;
        this.salesForecastRepository = salesForecastRepository;
    }
    
    /**
     * Get a sales forecast by its ID
     * 
     * @param id Forecast ID
     * @return The forecast
     */
    public SalesForecast getSalesForecastById(Long id) {
        return salesForecastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forecast not found with ID: " + id));
    }
    
    /**
     * Forecasts sales for the next month using the Moving Average method
     * 
     * @param brand The vehicle brand
     * @param model The vehicle model
     * @param packageType The vehicle package type
     * @param numberOfPeriods The number of previous periods to include in the average
     * @return The forecasted sales quantity
     */
    public SalesForecast forecastNextMonthSales(String brand, String model, String packageType, Integer year, int numberOfPeriods) {
        // Get current date
        LocalDate currentDate = LocalDate.now();
        
        // Calculate the start of next month and end of next month
        LocalDate nextMonthStart = currentDate.plusMonths(1).withDayOfMonth(1);
        LocalDate nextMonthEnd = nextMonthStart.with(TemporalAdjusters.lastDayOfMonth());
        
        // Get sales data for the past n months
        List<Long> monthlySales = new ArrayList<>();
        
        for (int i = 0; i < numberOfPeriods; i++) {
            LocalDate periodStart = currentDate.minusMonths(i + 1).withDayOfMonth(1);
            LocalDate periodEnd = periodStart.with(TemporalAdjusters.lastDayOfMonth());
            
            Long salesCount = saleRepository.countSalesForPeriod(brand, model, packageType, periodStart, periodEnd);
            monthlySales.add(salesCount != null ? salesCount : 0L);
        }
        
        // Calculate moving average
        int forecastedQuantity = calculateMovingAverage(monthlySales, numberOfPeriods);
        
        // Create and save forecast
        SalesForecast forecast = new SalesForecast();
        forecast.setBrand(brand);
        forecast.setModel(model);
        forecast.setPackageType(packageType);
        forecast.setYear(year);
        forecast.setForecastPeriodStart(nextMonthStart);
        forecast.setForecastPeriodEnd(nextMonthEnd);
        forecast.setForecastedQuantity(forecastedQuantity);
        forecast.setNumberOfPeriods(numberOfPeriods);
        forecast.setCreationDate(currentDate);
        
        return salesForecastRepository.save(forecast);
    }
    
    /**
     * Calculate moving average based on past sales data
     * 
     * @param salesData List of past sales quantities
     * @param n Number of periods to include
     * @return Forecasted sales quantity
     */
    private int calculateMovingAverage(List<Long> salesData, int n) {
        // Ensure we have enough data
        int dataSize = salesData.size();
        if (dataSize < n) {
            n = dataSize; // Use all available data if we don't have enough periods
        }
        
        // Sum the most recent n periods
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += salesData.get(i);
        }
        
        // Calculate average and round to nearest integer
        return (int) Math.round((double) sum / n);
    }
    
    /**
     * Update a forecast with the actual sales quantity
     * 
     * @param forecastId ID of the forecast to update
     * @param actualQuantity The actual sales quantity
     * @return The updated forecast
     */
    public SalesForecast updateForecastWithActual(Long forecastId, Integer actualQuantity) {
        SalesForecast forecast = salesForecastRepository.findById(forecastId)
                .orElseThrow(() -> new RuntimeException("Forecast not found with ID: " + forecastId));
        
        forecast.setActualQuantity(actualQuantity);
        return salesForecastRepository.save(forecast);
    }
} 