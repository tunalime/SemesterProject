package dev.tunalime.SemesterProject.controller;

import dev.tunalime.SemesterProject.dto.CustomerDTO;
import dev.tunalime.SemesterProject.entity.CustomerStatus;
import dev.tunalime.SemesterProject.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for customer operations
 */
@Controller
@RequestMapping("/customers")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    /**
     * Display all customers
     */
    @GetMapping
    public String getAllCustomers(Model model) {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customers/list";
    }
    
    /**
     * Display customer details
     */
    @GetMapping("/{id}")
    public String getCustomerDetails(@PathVariable Long id, Model model) {
        CustomerDTO customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        return "customers/details";
    }
    
    /**
     * Show form to add a new customer
     */
    @GetMapping("/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        model.addAttribute("statuses", CustomerStatus.values());
        return "customers/add";
    }
    
    /**
     * Process form to add a new customer
     */
    @PostMapping("/add")
    public String addCustomer(@Valid @ModelAttribute("customer") CustomerDTO customerDTO,
                            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "customers/add";
        }
        
        try {
            CustomerDTO savedCustomer = customerService.addCustomer(customerDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Customer added successfully");
            return "redirect:/customers/" + savedCustomer.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding customer: " + e.getMessage());
            return "redirect:/customers/add";
        }
    }
    
    /**
     * Show form to edit a customer
     */
    @GetMapping("/{id}/edit")
    public String showEditCustomerForm(@PathVariable Long id, Model model) {
        CustomerDTO customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("statuses", CustomerStatus.values());
        return "customers/edit";
    }
    
    /**
     * Process form to update a customer
     */
    @PostMapping("/{id}/edit")
    public String updateCustomer(@PathVariable Long id, @Valid @ModelAttribute("customer") CustomerDTO customerDTO,
                               BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "customers/edit";
        }
        
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Customer updated successfully");
            return "redirect:/customers/" + updatedCustomer.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating customer: " + e.getMessage());
            return "redirect:/customers/" + id + "/edit";
        }
    }
    
    /**
     * Update customer status
     */
    @PostMapping("/{id}/status")
    public String updateCustomerStatus(@PathVariable Long id, @RequestParam CustomerStatus status,
                                     RedirectAttributes redirectAttributes) {
        try {
            customerService.updateCustomerStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Customer status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating customer status: " + e.getMessage());
        }
        
        return "redirect:/customers/" + id;
    }
    
    /**
     * Delete a customer
     */
    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully");
            return "redirect:/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting customer: " + e.getMessage());
            return "redirect:/customers/" + id;
        }
    }
    
    /**
     * Search customers by name
     */
    @GetMapping("/search")
    public String searchCustomers(@RequestParam(required = false) String firstName,
                                 @RequestParam(required = false) String lastName,
                                 Model model) {
        List<CustomerDTO> customers = customerService.searchCustomersByName(firstName, lastName);
        model.addAttribute("customers", customers);
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        return "customers/search-results";
    }
    
    /**
     * Display customers by status
     */
    @GetMapping("/status/{status}")
    public String getCustomersByStatus(@PathVariable CustomerStatus status, Model model) {
        List<CustomerDTO> customers = customerService.getCustomersByStatus(status);
        model.addAttribute("customers", customers);
        model.addAttribute("status", status);
        return "customers/by-status";
    }
    
    /**
     * Display customers who have made purchases
     */
    @GetMapping("/with-purchases")
    public String getCustomersWithPurchases(Model model) {
        List<CustomerDTO> customers = customerService.getCustomersWithPurchases();
        model.addAttribute("customers", customers);
        model.addAttribute("title", "Customers With Purchases");
        return "customers/filtered-list";
    }
    
    /**
     * Display repeat customers
     */
    @GetMapping("/repeat-customers")
    public String getRepeatCustomers(Model model) {
        List<CustomerDTO> customers = customerService.getRepeatCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("title", "Repeat Customers");
        return "customers/filtered-list";
    }
    
    /**
     * Display customers registered between dates
     */
    @GetMapping("/registered")
    public String getCustomersRegisteredBetween(@RequestParam LocalDate startDate,
                                             @RequestParam LocalDate endDate,
                                             Model model) {
        List<CustomerDTO> customers = customerService.getCustomersRegisteredBetween(startDate, endDate);
        model.addAttribute("customers", customers);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("title", "Customers Registered Between " + startDate + " and " + endDate);
        return "customers/filtered-list";
    }
    
    /**
     * REST API endpoints for AJAX calls
     */
    
    /**
     * Get customers as JSON
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<CustomerDTO>> getCustomersApi() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }
    
    /**
     * Get customer details as JSON
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<CustomerDTO> getCustomerApi(@PathVariable Long id) {
        try {
            CustomerDTO customer = customerService.getCustomerById(id);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Search customers by name as JSON
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<CustomerDTO>> searchCustomersApi(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        
        List<CustomerDTO> customers = customerService.searchCustomersByName(firstName, lastName);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }
} 