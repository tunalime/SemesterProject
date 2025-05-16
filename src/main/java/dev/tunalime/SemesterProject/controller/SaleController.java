package dev.tunalime.SemesterProject.controller;

import dev.tunalime.SemesterProject.dto.CustomerDTO;
import dev.tunalime.SemesterProject.dto.VehicleDTO;
import dev.tunalime.SemesterProject.entity.*;
import dev.tunalime.SemesterProject.service.CustomerService;
import dev.tunalime.SemesterProject.service.SaleService;
import dev.tunalime.SemesterProject.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sales")
public class SaleController {
    
    private final SaleService saleService;
    private final CustomerService customerService;
    private final VehicleService vehicleService;
    
    @Autowired
    public SaleController(SaleService saleService, 
                         CustomerService customerService,
                         VehicleService vehicleService) {
        this.saleService = saleService;
        this.customerService = customerService;
        this.vehicleService = vehicleService;
    }
    
    /**
     * List all sales
     */
    @GetMapping
    public String listSales(Model model) {
        List<Sale> sales = saleService.getAllSales();
        model.addAttribute("sales", sales);
        return "sales/list";
    }
    
    /**
     * Show sale initiation form
     */
    @GetMapping("/initiate")
    public String showInitiateForm(Model model) {
        // Get all customers and available vehicles
        List<CustomerDTO> customers = customerService.getAllCustomers();
        List<VehicleDTO> availableVehicles = vehicleService.getVehiclesByStatuses(
            Arrays.asList(VehicleStatus.IN_STOCK, VehicleStatus.IN_SHOWROOM));
        
        model.addAttribute("customers", customers);
        model.addAttribute("vehicles", availableVehicles);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        
        return "sales/initiate";
    }
    
    /**
     * Process sale initiation
     */
    @PostMapping("/initiate")
    public String processInitiation(@RequestParam Long customerId,
                                   @RequestParam Long vehicleId,
                                   @RequestParam BigDecimal salePrice,
                                   @RequestParam(required = false) BigDecimal discount,
                                   @RequestParam PaymentMethod paymentMethod,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Initialize the sale
            Sale sale = saleService.initiateSale(customerId, vehicleId, salePrice, discount, paymentMethod);
            redirectAttributes.addFlashAttribute("success", "Sale initiated successfully");
            return "redirect:/sales/" + sale.getId() + "/details";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/sales/initiate";
        }
    }
    
    /**
     * Show sale details
     */
    @GetMapping("/{id}/details")
    public String showSaleDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Sale> saleOpt = saleService.getSaleById(id);
        
        if (saleOpt.isPresent()) {
            model.addAttribute("sale", saleOpt.get());
            model.addAttribute("paymentMethods", PaymentMethod.values());
            return "sales/details";
        } else {
            redirectAttributes.addFlashAttribute("error", "Sale not found");
            return "redirect:/sales";
        }
    }
    
    /**
     * Complete a sale
     */
    @PostMapping("/{id}/complete")
    public String completeSale(@PathVariable Long id,
                              @RequestParam String salesEmployeeName,
                              RedirectAttributes redirectAttributes) {
        try {
            saleService.completeSale(id, salesEmployeeName);
            redirectAttributes.addFlashAttribute("success", "Sale completed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/sales/" + id + "/details";
    }
    
    /**
     * Cancel a sale
     */
    @PostMapping("/{id}/cancel")
    public String cancelSale(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            saleService.cancelSale(id);
            redirectAttributes.addFlashAttribute("success", "Sale cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/sales/" + id + "/details";
    }
    
    /**
     * Mark a sale as delivered
     */
    @PostMapping("/{id}/deliver")
    public String markAsDelivered(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            saleService.markAsDelivered(id);
            redirectAttributes.addFlashAttribute("success", "Sale marked as delivered");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/sales/" + id + "/details";
    }
    
    /**
     * Update payment method
     */
    @PostMapping("/{id}/payment-method")
    public String updatePaymentMethod(@PathVariable Long id,
                                     @RequestParam PaymentMethod paymentMethod,
                                     RedirectAttributes redirectAttributes) {
        try {
            saleService.updatePaymentMethod(id, paymentMethod);
            redirectAttributes.addFlashAttribute("success", "Payment method updated");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/sales/" + id + "/details";
    }
    
    /**
     * Filter sales by status
     */
    @GetMapping("/filter")
    public String filterSales(@RequestParam(required = false) SaleStatus status, Model model) {
        List<Sale> sales;
        
        if (status != null) {
            sales = saleService.getSalesByStatus(status);
            model.addAttribute("currentFilter", status);
        } else {
            sales = saleService.getAllSales();
        }
        
        model.addAttribute("sales", sales);
        model.addAttribute("statuses", SaleStatus.values());
        return "sales/list";
    }
    
    /**
     * Customer sales history
     */
    @GetMapping("/customer/{customerId}")
    public String customerSalesHistory(@PathVariable Long customerId, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerDTO customer = customerService.getCustomerById(customerId);
            
            List<Sale> customerSales = saleService.getSalesByCustomerId(customerId);
            
            model.addAttribute("customer", customer);
            model.addAttribute("sales", customerSales);
            
            return "sales/customer-history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customers";
        }
    }
} 