package dev.tunalime.SemesterProject.controller;

import dev.tunalime.SemesterProject.dto.CustomerDTO;
import dev.tunalime.SemesterProject.dto.TestDriveDTO;
import dev.tunalime.SemesterProject.dto.VehicleDTO;
import dev.tunalime.SemesterProject.entity.TestDriveStatus;
import dev.tunalime.SemesterProject.service.CustomerService;
import dev.tunalime.SemesterProject.service.TestDriveService;
import dev.tunalime.SemesterProject.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for test drive operations
 */
@Controller
@RequestMapping("/test-drives")
public class TestDriveController {

    private final TestDriveService testDriveService;
    private final CustomerService customerService;
    private final VehicleService vehicleService;

    @Autowired
    public TestDriveController(TestDriveService testDriveService,
                               CustomerService customerService,
                               VehicleService vehicleService) {
        this.testDriveService = testDriveService;
        this.customerService = customerService;
        this.vehicleService = vehicleService;
    }

    /**
     * Display all test drives
     */
    @GetMapping
    public String getAllTestDrives(Model model) {
        List<TestDriveDTO> testDrives = testDriveService.getAllTestDrives();
        model.addAttribute("testDrives", testDrives);
        return "test-drives/list";
    }

    /**
     * Display test drive details
     */
    @GetMapping("/{id}")
    public String getTestDriveDetails(@PathVariable Long id, Model model) {
        TestDriveDTO testDrive = testDriveService.getTestDriveById(id);
        model.addAttribute("testDrive", testDrive);
        model.addAttribute("statuses", TestDriveStatus.values());
        return "test-drives/details";
    }

    /**
     * Show form to schedule a new test drive (via vehicle selection)
     */
    @GetMapping("/add")
    public String showAddTestDriveForm(@RequestParam(required = false) Long vehicleId,
                                      @RequestParam(required = false) Long customerId,
                                      Model model) {
        TestDriveDTO testDriveDTO = new TestDriveDTO();
        
        // If vehicle ID provided, pre-select the vehicle
        if (vehicleId != null) {
            VehicleDTO vehicle = vehicleService.getVehicleById(vehicleId);
            if (!vehicle.isAvailableForTestDrive()) {
                return "redirect:/vehicles?error=Vehicle+not+available+for+test+drive";
            }
            testDriveDTO.setVehicleId(vehicleId);
            testDriveDTO.setVehicleInfo(vehicle.getBrand() + " " + vehicle.getModel() + " " + vehicle.getYear());
            model.addAttribute("selectedVehicle", vehicle);
        }
        
        // If customer ID provided, pre-select the customer
        if (customerId != null) {
            CustomerDTO customer = customerService.getCustomerById(customerId);
            testDriveDTO.setCustomerId(customerId);
            testDriveDTO.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
            model.addAttribute("selectedCustomer", customer);
        }
        
        // For dropdown selections
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("availableVehicles", vehicleService.getVehiclesAvailableForTestDrive());
        model.addAttribute("testDrive", testDriveDTO);
        model.addAttribute("statuses", TestDriveStatus.values());
        
        return "test-drives/add";
    }

    /**
     * Process form to schedule a new test drive
     */
    @PostMapping("/add")
    public String addTestDrive(@Valid @ModelAttribute("testDrive") TestDriveDTO testDriveDTO,
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("availableVehicles", vehicleService.getVehiclesAvailableForTestDrive());
            model.addAttribute("statuses", TestDriveStatus.values());
            return "test-drives/add";
        }
        
        try {
            TestDriveDTO savedTestDrive = testDriveService.scheduleTestDrive(testDriveDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Test sürüşü başarıyla planlandı");
            return "redirect:/test-drives/" + savedTestDrive.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Test sürüşü planlanırken hata oluştu: " + e.getMessage());
            return "redirect:/test-drives/add";
        }
    }

    /**
     * Show form to edit a test drive
     */
    @GetMapping("/{id}/edit")
    public String showEditTestDriveForm(@PathVariable Long id, Model model) {
        TestDriveDTO testDrive = testDriveService.getTestDriveById(id);
        model.addAttribute("testDrive", testDrive);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("availableVehicles", vehicleService.getVehiclesAvailableForTestDrive());
        model.addAttribute("statuses", TestDriveStatus.values());
        return "test-drives/edit";
    }

    /**
     * Process form to update a test drive
     */
    @PostMapping("/{id}/edit")
    public String updateTestDrive(@PathVariable Long id, 
                                 @Valid @ModelAttribute("testDrive") TestDriveDTO testDriveDTO,
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("availableVehicles", vehicleService.getVehiclesAvailableForTestDrive());
            model.addAttribute("statuses", TestDriveStatus.values());
            return "test-drives/edit";
        }
        
        try {
            TestDriveDTO updatedTestDrive = testDriveService.updateTestDrive(id, testDriveDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Test sürüşü bilgileri başarıyla güncellendi");
            return "redirect:/test-drives/" + updatedTestDrive.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Test sürüşü güncellenirken hata oluştu: " + e.getMessage());
            return "redirect:/test-drives/" + id + "/edit";
        }
    }

    /**
     * Update test drive status
     */
    @PostMapping("/{id}/status")
    public String updateTestDriveStatus(@PathVariable Long id, 
                                       @RequestParam TestDriveStatus status,
                                       RedirectAttributes redirectAttributes) {
        try {
            testDriveService.updateTestDriveStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Test sürüşü durumu başarıyla güncellendi");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Test sürüşü durumu güncellenirken hata oluştu: " + e.getMessage());
        }
        
        return "redirect:/test-drives/" + id;
    }

    /**
     * Cancel a test drive
     */
    @PostMapping("/{id}/cancel")
    public String cancelTestDrive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            testDriveService.updateTestDriveStatus(id, TestDriveStatus.CANCELLED);
            redirectAttributes.addFlashAttribute("successMessage", "Test sürüşü iptal edildi");
            return "redirect:/test-drives";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Test sürüşü iptal edilirken hata oluştu: " + e.getMessage());
            return "redirect:/test-drives/" + id;
        }
    }

    /**
     * Delete a test drive
     */
    @PostMapping("/{id}/delete")
    public String deleteTestDrive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            testDriveService.deleteTestDrive(id);
            redirectAttributes.addFlashAttribute("successMessage", "Test sürüşü silindi");
            return "redirect:/test-drives";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Test sürüşü silinirken hata oluştu: " + e.getMessage());
            return "redirect:/test-drives/" + id;
        }
    }
    
    /**
     * REST API endpoints for AJAX calls
     */
    
    /**
     * Get test drives as JSON
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<TestDriveDTO>> getTestDrivesApi() {
        List<TestDriveDTO> testDrives = testDriveService.getAllTestDrives();
        return new ResponseEntity<>(testDrives, HttpStatus.OK);
    }
    
    /**
     * Get test drive details as JSON
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<TestDriveDTO> getTestDriveApi(@PathVariable Long id) {
        try {
            TestDriveDTO testDrive = testDriveService.getTestDriveById(id);
            return new ResponseEntity<>(testDrive, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Schedule a test drive via API
     */
    @PostMapping("/api/schedule")
    @ResponseBody
    public ResponseEntity<TestDriveDTO> scheduleTestDriveApi(@RequestBody TestDriveDTO testDriveDTO) {
        try {
            TestDriveDTO savedTestDrive = testDriveService.scheduleTestDrive(testDriveDTO);
            return new ResponseEntity<>(savedTestDrive, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
} 