package dev.tunalime.SemesterProject.service;

import dev.tunalime.SemesterProject.dto.VehicleDTO;
import dev.tunalime.SemesterProject.entity.StockItem;
import dev.tunalime.SemesterProject.entity.Vehicle;
import dev.tunalime.SemesterProject.entity.VehicleStatus;
import dev.tunalime.SemesterProject.repository.StockItemRepository;
import dev.tunalime.SemesterProject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for vehicle operations
 */
@Service
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final StockItemRepository stockItemRepository;
    
    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, StockItemRepository stockItemRepository) {
        this.vehicleRepository = vehicleRepository;
        this.stockItemRepository = stockItemRepository;
    }
    
    /**
     * Get all vehicles
     * 
     * @return List of all vehicles
     */
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get vehicle by ID
     * 
     * @param id Vehicle ID
     * @return Vehicle DTO
     */
    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + id));
        return convertToDTO(vehicle);
    }
    
    /**
     * Add a new vehicle to inventory
     * 
     * @param vehicleDTO Vehicle information
     * @return Added vehicle
     */
    @Transactional
    public VehicleDTO addVehicle(VehicleDTO vehicleDTO) {
        // Check if this vehicle type exists in stock
        Optional<StockItem> stockItemOpt = stockItemRepository.findByBrandAndModelAndYearAndPackageType(
                vehicleDTO.getBrand(), vehicleDTO.getModel(), vehicleDTO.getYear(), vehicleDTO.getPackageType());
        
        StockItem stockItem;
        
        if (stockItemOpt.isPresent()) {
            // Update the existing stock item's quantity
            stockItem = stockItemOpt.get();
            stockItem.setTotalQuantity(stockItem.getTotalQuantity() + 1);
        } else {
            // Create a new stock item for this vehicle type
            stockItem = new StockItem();
            stockItem.setBrand(vehicleDTO.getBrand());
            stockItem.setModel(vehicleDTO.getModel());
            stockItem.setYear(vehicleDTO.getYear());
            stockItem.setPackageType(vehicleDTO.getPackageType());
            stockItem.setBasePrice(vehicleDTO.getPrice());
            stockItem.setTotalQuantity(1);
        }
        
        // Save or update the stock item
        stockItem = stockItemRepository.save(stockItem);
        
        // Create the vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setYear(vehicleDTO.getYear());
        vehicle.setPackageType(vehicleDTO.getPackageType());
        vehicle.setVin(vehicleDTO.getVin());
        vehicle.setPrice(vehicleDTO.getPrice());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setMileage(vehicleDTO.getMileage());
        vehicle.setStockEntryDate(LocalDate.now());
        vehicle.setStatus(VehicleStatus.IN_STOCK);
        vehicle.setDescription(vehicleDTO.getDescription());
        vehicle.setAvailableForTestDrive(vehicleDTO.isAvailableForTestDrive());
        vehicle.setStockItem(stockItem);
        
        // Save the vehicle
        vehicle = vehicleRepository.save(vehicle);
        
        return convertToDTO(vehicle);
    }
    
    /**
     * Update vehicle information
     * 
     * @param id Vehicle ID
     * @param vehicleDTO Updated vehicle information
     * @return Updated vehicle
     */
    @Transactional
    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + id));
        
        // Handle stock item changes if brand, model, year, or package changed
        if (!vehicle.getBrand().equals(vehicleDTO.getBrand()) || 
            !vehicle.getModel().equals(vehicleDTO.getModel()) || 
            !vehicle.getYear().equals(vehicleDTO.getYear()) || 
            !vehicle.getPackageType().equals(vehicleDTO.getPackageType())) {
            
            // Decrease count in the old stock item
            StockItem oldStockItem = vehicle.getStockItem();
            oldStockItem.setTotalQuantity(oldStockItem.getTotalQuantity() - 1);

            stockItemRepository.save(oldStockItem);
            
            // Find or create new stock item
            Optional<StockItem> newStockItemOpt = stockItemRepository.findByBrandAndModelAndYearAndPackageType(
                    vehicleDTO.getBrand(), vehicleDTO.getModel(), vehicleDTO.getYear(), vehicleDTO.getPackageType());
            
            StockItem newStockItem;
            if (newStockItemOpt.isPresent()) {
                newStockItem = newStockItemOpt.get();
                newStockItem.setTotalQuantity(newStockItem.getTotalQuantity() + 1);
            } else {
                newStockItem = new StockItem();
                newStockItem.setBrand(vehicleDTO.getBrand());
                newStockItem.setModel(vehicleDTO.getModel());
                newStockItem.setYear(vehicleDTO.getYear());
                newStockItem.setPackageType(vehicleDTO.getPackageType());
                newStockItem.setBasePrice(vehicleDTO.getPrice());
                newStockItem.setTotalQuantity(1);
            }
            
            newStockItem = stockItemRepository.save(newStockItem);
            vehicle.setStockItem(newStockItem);
        }
        
        // Update vehicle properties
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setYear(vehicleDTO.getYear());
        vehicle.setPackageType(vehicleDTO.getPackageType());
        vehicle.setVin(vehicleDTO.getVin());
        vehicle.setPrice(vehicleDTO.getPrice());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setMileage(vehicleDTO.getMileage());
        vehicle.setDescription(vehicleDTO.getDescription());
        vehicle.setAvailableForTestDrive(vehicleDTO.isAvailableForTestDrive());
        
        // Save the updated vehicle
        vehicle = vehicleRepository.save(vehicle);
        
        return convertToDTO(vehicle);
    }
    
    /**
     * Change vehicle status
     * 
     * @param id Vehicle ID
     * @param newStatus New status
     * @return Updated vehicle
     */
    @Transactional
    public VehicleDTO changeVehicleStatus(Long id, VehicleStatus newStatus) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + id));
        
        VehicleStatus oldStatus = vehicle.getStatus();
        vehicle.setStatus(newStatus);
        
        // Update stock item available quantity if necessary
        StockItem stockItem = vehicle.getStockItem();
        
        stockItemRepository.save(stockItem);
        vehicle = vehicleRepository.save(vehicle);
        
        return convertToDTO(vehicle);
    }
    
    /**
     * Delete a vehicle
     * 
     * @param id Vehicle ID
     */
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + id));
        
        // Update stock item quantities
        StockItem stockItem = vehicle.getStockItem();
        stockItem.setTotalQuantity(stockItem.getTotalQuantity() - 1);

        stockItemRepository.save(stockItem);
        vehicleRepository.delete(vehicle);
    }
    
    /**
     * Search vehicles by various criteria
     * 
     * @param brand Brand (optional)
     * @param model Model (optional)
     * @param year Year (optional)
     * @param packageType Package type (optional)
     * @param status Status (optional)
     * @return List of matching vehicles
     */
    public List<VehicleDTO> searchVehicles(String brand, String model, Integer year, String packageType, VehicleStatus status) {
        List<Vehicle> vehicles;
        
        if (brand != null && model != null && year != null && packageType != null && status != null) {
            vehicles = vehicleRepository.findByBrandAndModelAndYearAndPackageTypeAndStatus(brand, model, year, packageType, status);
        } else if (brand != null && model != null && year != null && packageType != null) {
            vehicles = vehicleRepository.findByBrandAndModelAndYearAndPackageType(brand, model, year, packageType);
        } else if (brand != null && model != null && year != null) {
            vehicles = vehicleRepository.findByBrandAndModelAndYear(brand, model, year);
        } else if (brand != null && model != null) {
            vehicles = vehicleRepository.findByBrandAndModel(brand, model);
        } else if (brand != null) {
            vehicles = vehicleRepository.findByBrand(brand);
        } else if (model != null) {
            vehicles = vehicleRepository.findByModel(model);
        } else if (year != null) {
            vehicles = vehicleRepository.findByYear(year);
        } else if (packageType != null) {
            vehicles = vehicleRepository.findByPackageType(packageType);
        } else if (status != null) {
            vehicles = vehicleRepository.findByStatus(status);
        } else {
            vehicles = vehicleRepository.findAll();
        }
        
        return vehicles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get vehicles available for test drive
     * 
     * @return List of vehicles available for test drive
     */
    public List<VehicleDTO> getVehiclesAvailableForTestDrive() {
        return vehicleRepository.findByIsAvailableForTestDriveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Vehicle entity to DTO
     * 
     * @param vehicle Vehicle entity
     * @return Vehicle DTO
     */
    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setYear(vehicle.getYear());
        dto.setPackageType(vehicle.getPackageType());
        dto.setVin(vehicle.getVin());
        dto.setPrice(vehicle.getPrice());
        dto.setColor(vehicle.getColor());
        dto.setMileage(vehicle.getMileage());
        dto.setStockEntryDate(vehicle.getStockEntryDate());
        dto.setStatus(vehicle.getStatus());
        dto.setDescription(vehicle.getDescription());
        dto.setAvailableForTestDrive(vehicle.isAvailableForTestDrive());
        
        if (vehicle.getStockItem() != null) {
            dto.setStockItemId(vehicle.getStockItem().getId());
        }
        
        return dto;
    }
} 