package dev.tunalime.SemesterProject.controller;

import dev.tunalime.SemesterProject.dto.VehicleDTO;
import dev.tunalime.SemesterProject.entity.VehicleStatus;
import dev.tunalime.SemesterProject.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for vehicle operations
 */
@Controller
@RequestMapping("/vehicles")
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }
    
    /**
     * Display all vehicles
     */
    @GetMapping
    public String getAllVehicles(Model model) {
        List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "vehicles/list";
    }
    
    /**
     * Display vehicle details
     */
    @GetMapping("/{id}")
    public String getVehicleDetails(@PathVariable Long id, Model model) {
        VehicleDTO vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        return "vehicles/details";
    }
    
    /**
     * Show form to add a new vehicle
     */
    @GetMapping("/add")
    public String showAddVehicleForm(Model model) {
        model.addAttribute("vehicle", new VehicleDTO());
        model.addAttribute("statuses", VehicleStatus.values());
        return "vehicles/add";
    }
    
    /**
     * Process form to add a new vehicle
     */
    @PostMapping("/add")
    public String addVehicle(@Valid @ModelAttribute("vehicle") VehicleDTO vehicleDTO,
                            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "vehicles/add";
        }
        
        try {
            VehicleDTO savedVehicle = vehicleService.addVehicle(vehicleDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle added successfully");
            return "redirect:/vehicles/" + savedVehicle.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding vehicle: " + e.getMessage());
            return "redirect:/vehicles/add";
        }
    }
    
    /**
     * Show form to edit a vehicle
     */
    @GetMapping("/{id}/edit")
    public String showEditVehicleForm(@PathVariable Long id, Model model) {
        VehicleDTO vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("statuses", VehicleStatus.values());
        return "vehicles/edit";
    }
    
    /**
     * Process form to update a vehicle
     */
    @PostMapping("/{id}/edit")
    public String updateVehicle(@PathVariable Long id, @Valid @ModelAttribute("vehicle") VehicleDTO vehicleDTO,
                               BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "vehicles/edit";
        }
        
        try {
            VehicleDTO updatedVehicle = vehicleService.updateVehicle(id, vehicleDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle updated successfully");
            return "redirect:/vehicles/" + updatedVehicle.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating vehicle: " + e.getMessage());
            return "redirect:/vehicles/" + id + "/edit";
        }
    }
    
    /**
     * Change vehicle status
     */
    @PostMapping("/{id}/status")
    public String changeVehicleStatus(@PathVariable Long id, @RequestParam VehicleStatus status,
                                     RedirectAttributes redirectAttributes) {
        try {
            vehicleService.changeVehicleStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating vehicle status: " + e.getMessage());
        }
        
        return "redirect:/vehicles/" + id;
    }
    
    /**
     * Delete a vehicle
     */
    @PostMapping("/{id}/delete")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle deleted successfully");
            return "redirect:/vehicles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting vehicle: " + e.getMessage());
            return "redirect:/vehicles/" + id;
        }
    }
    
    /**
     * Search vehicles
     */
    @GetMapping("/search")
    public String searchVehicles(@RequestParam(required = false) String brand,
                                @RequestParam(required = false) String model,
                                @RequestParam(required = false) Integer year,
                                @RequestParam(required = false) String packageType,
                                @RequestParam(required = false) VehicleStatus status,
                                Model modelObj) {
        List<VehicleDTO> vehicles = vehicleService.searchVehicles(brand, model, year, packageType, status);
        modelObj.addAttribute("vehicles", vehicles);
        modelObj.addAttribute("brand", brand);
        modelObj.addAttribute("model", model);
        modelObj.addAttribute("year", year);
        modelObj.addAttribute("packageType", packageType);
        modelObj.addAttribute("status", status);
        return "vehicles/search-results";
    }
    
    /**
     * Show vehicles available for test drive
     */
    @GetMapping("/test-drive")
    public String getVehiclesForTestDrive(Model model) {
        List<VehicleDTO> vehicles = vehicleService.getVehiclesAvailableForTestDrive();
        model.addAttribute("vehicles", vehicles);
        return "vehicles/test-drive";
    }
    
    /**
     * REST API endpoints for AJAX calls
     */
    
    /**
     * Get vehicles as JSON
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<VehicleDTO>> getVehiclesApi() {
        List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }
    
    /**
     * Get vehicle details as JSON
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<VehicleDTO> getVehicleApi(@PathVariable Long id) {
        try {
            VehicleDTO vehicle = vehicleService.getVehicleById(id);
            return new ResponseEntity<>(vehicle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Search vehicles as JSON
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<VehicleDTO>> searchVehiclesApi(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String packageType,
            @RequestParam(required = false) VehicleStatus status) {
        
        List<VehicleDTO> vehicles = vehicleService.searchVehicles(brand, model, year, packageType, status);
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }
} 