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

import java.util.List;

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
            redirectAttributes.addFlashAttribute("successMessage", "Forecast generated successfully");
            return "redirect:/forecasts/" + forecast.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error generating forecast: " + e.getMessage());
            return "redirect:/forecasts/generate";
        }
    }
    
    /**
     * Display forecast details
     */
    @GetMapping("/{id}")
    public String getForecastDetails(@PathVariable Long id, Model model) {
        SalesForecast forecast = forecastService.getSalesForecastById(id);
        model.addAttribute("forecast", convertToDTO(forecast));
        return "forecasts/details";
    }
    
    /**
     * Update forecast with actual sales
     */
    @PostMapping("/{id}/update-actual")
    public String updateForecastWithActual(@PathVariable Long id,
                                         @RequestParam Integer actualQuantity,
                                         RedirectAttributes redirectAttributes) {
        try {
            SalesForecast forecast = forecastService.updateForecastWithActual(id, actualQuantity);
            redirectAttributes.addFlashAttribute("successMessage", "Forecast updated with actual sales");
            return "redirect:/forecasts/" + forecast.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating forecast: " + e.getMessage());
            return "redirect:/forecasts/" + id;
        }
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