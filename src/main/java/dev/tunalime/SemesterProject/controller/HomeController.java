package dev.tunalime.SemesterProject.controller;

import dev.tunalime.SemesterProject.entity.SaleStatus;
import dev.tunalime.SemesterProject.entity.VehicleStatus;
import dev.tunalime.SemesterProject.repository.CustomerRepository;
import dev.tunalime.SemesterProject.repository.SaleRepository;
import dev.tunalime.SemesterProject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for home page and dashboard
 */
@Controller
public class HomeController {
    
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;
    
    @Autowired
    public HomeController(VehicleRepository vehicleRepository, 
                        CustomerRepository customerRepository,
                        SaleRepository saleRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.saleRepository = saleRepository;
    }
    
    /**
     * Home page / dashboard
     */
    @GetMapping("/")
    public String home(Model model) {
        // Count vehicles by status
        long totalVehicles = vehicleRepository.count();
        long inStockVehicles = vehicleRepository.findByStatus(VehicleStatus.IN_STOCK).size();
        long inShowroomVehicles = vehicleRepository.findByStatus(VehicleStatus.IN_SHOWROOM).size();
        long reservedVehicles = vehicleRepository.findByStatus(VehicleStatus.RESERVED).size();
        long soldVehicles = vehicleRepository.findByStatus(VehicleStatus.SOLD).size();
        
        model.addAttribute("totalVehicles", totalVehicles);
        model.addAttribute("inStockVehicles", inStockVehicles);
        model.addAttribute("inShowroomVehicles", inShowroomVehicles);
        model.addAttribute("reservedVehicles", reservedVehicles);
        model.addAttribute("soldVehicles", soldVehicles);
        
        // Customer stats
        long totalCustomers = customerRepository.count();
        model.addAttribute("totalCustomers", totalCustomers);
        
        // Sales stats
        long totalSales = saleRepository.count();
        long completedSales = saleRepository.findByStatus(SaleStatus.COMPLETED).size();
        long pendingSales = saleRepository.findByStatus(SaleStatus.PENDING).size();
        
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("completedSales", completedSales);
        model.addAttribute("pendingSales", pendingSales);
        
        // Monthly sales data for current year
        int currentYear = Year.now().getValue();
        Map<String, Long> monthlySales = new HashMap<>();
        
        saleRepository.countSalesByMonthForYear(currentYear).forEach(data -> {
            int month = ((Number) data[0]).intValue();
            long count = ((Number) data[1]).longValue();
            
            // Map month number to month name
            String monthName = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthlySales.put(monthName, count);
        });
        
        model.addAttribute("monthlySales", monthlySales);
        model.addAttribute("currentYear", currentYear);
        
        // Recent sales
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long recentSales = saleRepository.findBySaleDateBetween(thirtyDaysAgo, LocalDate.now()).size();
        model.addAttribute("recentSales", recentSales);
        
        return "home";
    }
    
    /**
     * About page
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
} 