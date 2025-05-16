package dev.tunalime.SemesterProject.controller;

import dev.tunalime.SemesterProject.dto.SalesForecastDTO;
import dev.tunalime.SemesterProject.entity.SalesForecast;
import dev.tunalime.SemesterProject.repository.VehicleRepository;
import dev.tunalime.SemesterProject.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for sales forecasting operations
 */
@Controller
@RequestMapping("/forecasts")
public class ForecastController {
    
    private final ForecastService forecastService;
    private final VehicleRepository vehicleRepository;
    
    @Autowired
    public ForecastController(ForecastService forecastService, VehicleRepository vehicleRepository) {
        this.forecastService = forecastService;
        this.vehicleRepository = vehicleRepository;
    }
    
    /**
     * Display forecast form
     */
    @GetMapping("/generate")
    public String showForecastForm(Model model) {
        // Populate dropdown options for vehicle attributes
        model.addAttribute("brands", vehicleRepository.findAllDistinctBrands());
        
        // Add default periods options
        model.addAttribute("periodOptions", new Integer[]{3, 4, 5, 6, 12});
        return "forecasts/generate";
    }
    
    /**
     * Get models for a selected brand (AJAX)
     */
    @GetMapping("/api/models")
    @ResponseBody
    public ResponseEntity<List<String>> getModelsByBrand(@RequestParam String brand) {
        List<String> models = vehicleRepository.findAllDistinctModelsByBrand(brand);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }
    
    /**
     * Get years for a selected brand and model (AJAX)
     */
    @GetMapping("/api/years")
    @ResponseBody
    public ResponseEntity<List<Integer>> getYearsByBrandAndModel(
            @RequestParam String brand, @RequestParam String model) {
        List<Integer> years = vehicleRepository.findAllDistinctYearsByBrandAndModel(brand, model);
        return new ResponseEntity<>(years, HttpStatus.OK);
    }
    
    /**
     * Get package types for a selected brand, model, and year (AJAX)
     */
    @GetMapping("/api/packages")
    @ResponseBody
    public ResponseEntity<List<String>> getPackagesByBrandAndModelAndYear(
            @RequestParam String brand, @RequestParam String model, @RequestParam Integer year) {
        List<String> packages = vehicleRepository.findAllDistinctPackageTypesByBrandAndModelAndYear(brand, model, year);
        return new ResponseEntity<>(packages, HttpStatus.OK);
    }
    
    /**
     * Generate forecast
     */
    @PostMapping("/generate")
    public String generateForecast(@RequestParam String brand,
                                 @RequestParam String model,
                                 @RequestParam String packageType,
                                 @RequestParam Integer year,
                                 @RequestParam Integer numberOfPeriods,
                                 RedirectAttributes redirectAttributes) {
        try {
            SalesForecast forecast = forecastService.forecastNextMonthSales(brand, model, packageType, year, numberOfPeriods);
            redirectAttributes.addFlashAttribute("successMessage", "Tahmin başarıyla oluşturuldu");
            return "redirect:/forecasts/" + forecast.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tahmin oluşturulurken hata oluştu: " + e.getMessage());
            return "redirect:/forecasts/generate";
        }
    }
    
    /**
     * Display forecast details
     */
    @GetMapping("/{id}")
    public String getForecastDetails(@PathVariable Long id, Model model) {
        SalesForecast forecast = forecastService.getSalesForecastById(id);
        
        // Get historical sales data for chart
        List<Object[]> historicalSales = forecastService.getHistoricalSalesByMonth(
            forecast.getBrand(), 
            forecast.getModel(), 
            forecast.getPackageType(), 
            forecast.getYear(),
            forecast.getNumberOfPeriods()
        );
        
        model.addAttribute("forecast", convertToDTO(forecast));
        model.addAttribute("historicalSales", historicalSales);
        
        return "forecasts/details";
    }
    
    /**
     * Get historical sales data for a specific vehicle configuration (AJAX)
     */
    @GetMapping("/api/historical-sales")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getHistoricalSalesData(
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam String packageType,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "12") Integer months) {
        
        List<Object[]> historicalData = forecastService.getHistoricalSalesByMonth(
            brand, model, packageType, year, months);
        
        Map<String, Object> response = new HashMap<>();
        response.put("brand", brand);
        response.put("model", model);
        response.put("packageType", packageType);
        response.put("year", year);
        response.put("historicalData", historicalData);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Generate forecast via API (AJAX)
     */
    @PostMapping("/api/generate")
    @ResponseBody
    public ResponseEntity<SalesForecastDTO> generateForecastApi(
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam String packageType,
            @RequestParam(required = false) Integer year,
            @RequestParam Integer numberOfPeriods) {
        
        SalesForecast forecast = forecastService.forecastNextMonthSales(
            brand, model, packageType, year, numberOfPeriods);
        
        return new ResponseEntity<>(convertToDTO(forecast), HttpStatus.OK);
    }
    
    /**
     * Convert SalesForecast entity to DTO
     */
    private SalesForecastDTO convertToDTO(SalesForecast forecast) {
        return new SalesForecastDTO(
                forecast.getId(),
                forecast.getBrand(),
                forecast.getModel(),
                forecast.getPackageType(),
                forecast.getYear(),
                forecast.getForecastPeriodStart(),
                forecast.getForecastPeriodEnd(),
                forecast.getForecastedQuantity(),
                forecast.getActualQuantity(),
                forecast.getNumberOfPeriods(),
                forecast.getCreationDate()
        );
    }
} 