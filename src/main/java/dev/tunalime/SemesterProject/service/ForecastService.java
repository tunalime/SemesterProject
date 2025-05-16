package dev.tunalime.SemesterProject.service;

import dev.tunalime.SemesterProject.entity.SalesForecast;
import dev.tunalime.SemesterProject.entity.Sale;
import dev.tunalime.SemesterProject.repository.SaleRepository;
import dev.tunalime.SemesterProject.repository.SalesForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
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
        SalesForecast forecast = salesForecastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forecast not found with ID: " + id));
        
        // Automatically calculate actual sales from database if forecast period has ended
        if (LocalDate.now().isAfter(forecast.getForecastPeriodEnd())) {
            int actualSales = calculateActualSales(forecast);
            forecast.setActualQuantity(actualSales);
            forecast = salesForecastRepository.save(forecast);
        }
        
        return forecast;
    }
    
    /**
     * Calculate actual sales for a forecast period from the database
     * 
     * @param forecast The forecast entity
     * @return The actual sales quantity
     */
    private int calculateActualSales(SalesForecast forecast) {
        // Tüm veritabanındaki satışları getir
        List<Sale> allSales = saleRepository.findAll();
        
        // Bu araç modeline ait olan ve dönem içindeki satışları filtrele
        long actualSales = allSales.stream()
            .filter(sale -> 
                sale.getVehicle().getBrand().equalsIgnoreCase(forecast.getBrand()) &&
                sale.getVehicle().getModel().equalsIgnoreCase(forecast.getModel()) &&
                !sale.getSaleDate().isBefore(forecast.getForecastPeriodStart()) &&
                !sale.getSaleDate().isAfter(forecast.getForecastPeriodEnd()))
            .count();
        
        return (int) actualSales;
    }
    
    /**
     * Forecasts sales for a given vehicle model using Moving Average method
     * 
     * @param brand The vehicle brand
     * @param model The vehicle model
     * @param packageType The vehicle package type
     * @param year The model year
     * @param numberOfPeriods The number of periods to use in the moving average calculation
     * @return The forecasted sales
     */
    public SalesForecast forecastNextMonthSales(String brand, String model, String packageType, Integer year, int numberOfPeriods) {
        // Get current date
        LocalDate currentDate = LocalDate.now();
        
        // Calculate the start of next month and end of next month
        LocalDate nextMonthStart = currentDate.plusMonths(1).withDayOfMonth(1);
        LocalDate nextMonthEnd = nextMonthStart.with(TemporalAdjusters.lastDayOfMonth());
        
        // Collect sales data for the last 'numberOfPeriods' months
        List<Integer> monthlySales = new ArrayList<>();
        
        // Start from current month and go back 'numberOfPeriods' months
        for (int i = 0; i < numberOfPeriods; i++) {
            YearMonth currentYearMonth = YearMonth.from(currentDate.minusMonths(i));
            LocalDate periodStart = currentYearMonth.atDay(1);
            LocalDate periodEnd = currentYearMonth.atEndOfMonth();
            
            // Query database for sales in this period for the specified vehicle
            Long salesInPeriod;
            if (year != null) {
                // If year is specified, filter by year as well
                salesInPeriod = saleRepository.countSalesForPeriod(
                    brand, model, packageType, year, periodStart, periodEnd);
            } else {
                // If year is not specified, use the flexible query
                salesInPeriod = saleRepository.countSalesForPeriodFlexible(
                    brand, model, packageType, periodStart, periodEnd);
            }
            
            monthlySales.add(salesInPeriod.intValue());
        }
        
        // Calculate moving average: HO(n) = (y_t + y_t-1 + ... + y_t-n+1) / n
        int forecastedQuantity = 0;
        if (!monthlySales.isEmpty()) {
            int sum = monthlySales.stream().mapToInt(Integer::intValue).sum();
            forecastedQuantity = (int) Math.ceil(sum / (double) numberOfPeriods);
        }
        
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
     * Get historical sales data by month for a specific vehicle
     * This can be used to display alongside the forecast
     * 
     * @param brand The vehicle brand
     * @param model The vehicle model
     * @param packageType The vehicle package type
     * @param year The model year
     * @param months Number of months to retrieve
     * @return List of monthly sales counts
     */
    public List<Object[]> getHistoricalSalesByMonth(String brand, String model, String packageType, Integer year, int months) {
        LocalDate currentDate = LocalDate.now();
        List<Object[]> monthlySales = new ArrayList<>();
        
        for (int i = months - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(currentDate.minusMonths(i));
            LocalDate periodStart = yearMonth.atDay(1);
            LocalDate periodEnd = yearMonth.atEndOfMonth();
            
            Long salesCount;
            if (year != null) {
                salesCount = saleRepository.countSalesForPeriod(
                    brand, model, packageType, year, periodStart, periodEnd);
            } else {
                salesCount = saleRepository.countSalesForPeriodFlexible(
                    brand, model, packageType, periodStart, periodEnd);
            }
            
            monthlySales.add(new Object[]{yearMonth.toString(), salesCount});
        }
        
        return monthlySales;
    }
} 