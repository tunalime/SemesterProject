package dev.tunalime.SemesterProject.service;

import dev.tunalime.SemesterProject.dto.TestDriveDTO;
import dev.tunalime.SemesterProject.entity.Customer;
import dev.tunalime.SemesterProject.entity.TestDrive;
import dev.tunalime.SemesterProject.entity.TestDriveStatus;
import dev.tunalime.SemesterProject.entity.Vehicle;
import dev.tunalime.SemesterProject.repository.CustomerRepository;
import dev.tunalime.SemesterProject.repository.TestDriveRepository;
import dev.tunalime.SemesterProject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for test drive operations
 */
@Service
public class TestDriveService {
    
    private final TestDriveRepository testDriveRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    
    @Autowired
    public TestDriveService(TestDriveRepository testDriveRepository,
                          CustomerRepository customerRepository,
                          VehicleRepository vehicleRepository) {
        this.testDriveRepository = testDriveRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
    }
    
    /**
     * Get all test drives
     * 
     * @return List of all test drives
     */
    public List<TestDriveDTO> getAllTestDrives() {
        return testDriveRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get test drive by ID
     * 
     * @param id Test drive ID
     * @return Test drive DTO
     */
    public TestDriveDTO getTestDriveById(Long id) {
        TestDrive testDrive = testDriveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test drive not found with ID: " + id));
        return convertToDTO(testDrive);
    }
    
    /**
     * Get test drives by customer ID
     * 
     * @param customerId Customer ID
     * @return List of test drives for the customer
     */
    public List<TestDriveDTO> getTestDrivesByCustomer(Long customerId) {
        return testDriveRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get test drives by vehicle ID
     * 
     * @param vehicleId Vehicle ID
     * @return List of test drives for the vehicle
     */
    public List<TestDriveDTO> getTestDrivesByVehicle(Long vehicleId) {
        return testDriveRepository.findByVehicleId(vehicleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get test drives by status
     * 
     * @param status Test drive status
     * @return List of test drives with the specified status
     */
    public List<TestDriveDTO> getTestDrivesByStatus(TestDriveStatus status) {
        return testDriveRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get test drives scheduled for today
     * 
     * @return List of test drives scheduled for today
     */
    public List<TestDriveDTO> getTestDrivesForToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusSeconds(1);
        
        return testDriveRepository.findScheduledTestDrivesForToday(startOfDay, endOfDay).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Schedule a new test drive
     * 
     * @param testDriveDTO Test drive information
     * @return Scheduled test drive
     */
    public TestDriveDTO scheduleTestDrive(TestDriveDTO testDriveDTO) {
        // Check if customer exists
        Customer customer = customerRepository.findById(testDriveDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + testDriveDTO.getCustomerId()));
        
        // Check if vehicle exists and is available for test drive
        Vehicle vehicle = vehicleRepository.findById(testDriveDTO.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + testDriveDTO.getVehicleId()));
        
        if (!vehicle.isAvailableForTestDrive()) {
            throw new RuntimeException("Vehicle with ID: " + testDriveDTO.getVehicleId() + " is not available for test drive");
        }
        
        // Create and save the test drive entity
        TestDrive testDrive = new TestDrive();
        testDrive.setCustomer(customer);
        testDrive.setVehicle(vehicle);
        testDrive.setScheduledDateTime(testDriveDTO.getScheduledDateTime());
        testDrive.setStatus(TestDriveStatus.SCHEDULED);
        testDrive.setStaffMemberName(testDriveDTO.getStaffMemberName());
        
        testDrive = testDriveRepository.save(testDrive);
        
        return convertToDTO(testDrive);
    }
    
    /**
     * Update test drive information
     * 
     * @param id Test drive ID
     * @param testDriveDTO Updated test drive information
     * @return Updated test drive
     */
    public TestDriveDTO updateTestDrive(Long id, TestDriveDTO testDriveDTO) {
        TestDrive testDrive = testDriveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test drive not found with ID: " + id));
        
        // Update test drive fields
        testDrive.setScheduledDateTime(testDriveDTO.getScheduledDateTime());
        testDrive.setActualStartTime(testDriveDTO.getActualStartTime());
        testDrive.setActualEndTime(testDriveDTO.getActualEndTime());
        testDrive.setStatus(testDriveDTO.getStatus());
        testDrive.setCustomerFeedback(testDriveDTO.getCustomerFeedback());
        testDrive.setStaffMemberName(testDriveDTO.getStaffMemberName());
        
        // If customer or vehicle changed, update them too
        if (testDriveDTO.getCustomerId() != null && 
                !testDriveDTO.getCustomerId().equals(testDrive.getCustomer().getId())) {
            Customer customer = customerRepository.findById(testDriveDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + testDriveDTO.getCustomerId()));
            testDrive.setCustomer(customer);
        }
        
        if (testDriveDTO.getVehicleId() != null && 
                !testDriveDTO.getVehicleId().equals(testDrive.getVehicle().getId())) {
            Vehicle vehicle = vehicleRepository.findById(testDriveDTO.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + testDriveDTO.getVehicleId()));
            
            if (!vehicle.isAvailableForTestDrive()) {
                throw new RuntimeException("Vehicle with ID: " + testDriveDTO.getVehicleId() + " is not available for test drive");
            }
            
            testDrive.setVehicle(vehicle);
        }
        
        testDrive = testDriveRepository.save(testDrive);
        
        return convertToDTO(testDrive);
    }
    
    /**
     * Update test drive status
     * 
     * @param id Test drive ID
     * @param status New status
     * @return Updated test drive
     */
    public TestDriveDTO updateTestDriveStatus(Long id, TestDriveStatus status) {
        TestDrive testDrive = testDriveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test drive not found with ID: " + id));
        
        testDrive.setStatus(status);
        
        // If completed, set actual times if not already set
        if (status == TestDriveStatus.COMPLETED) {
            LocalDateTime now = LocalDateTime.now();
            
            if (testDrive.getActualStartTime() == null) {
                // If start time not set, set it to scheduled time
                testDrive.setActualStartTime(testDrive.getScheduledDateTime());
            }
            
            if (testDrive.getActualEndTime() == null) {
                // Set end time to now
                testDrive.setActualEndTime(now);
            }
        }
        
        testDrive = testDriveRepository.save(testDrive);
        
        return convertToDTO(testDrive);
    }
    
    /**
     * Delete a test drive
     * 
     * @param id Test drive ID
     */
    public void deleteTestDrive(Long id) {
        TestDrive testDrive = testDriveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test drive not found with ID: " + id));
        
        testDriveRepository.delete(testDrive);
    }
    
    /**
     * Convert TestDrive entity to DTO
     * 
     * @param testDrive TestDrive entity
     * @return TestDrive DTO
     */
    private TestDriveDTO convertToDTO(TestDrive testDrive) {
        TestDriveDTO dto = new TestDriveDTO();
        dto.setId(testDrive.getId());
        dto.setCustomerId(testDrive.getCustomer().getId());
        dto.setCustomerName(testDrive.getCustomer().getFirstName() + " " + testDrive.getCustomer().getLastName());
        dto.setVehicleId(testDrive.getVehicle().getId());
        dto.setVehicleInfo(testDrive.getVehicle().getBrand() + " " + 
                testDrive.getVehicle().getModel() + " " + 
                testDrive.getVehicle().getYear());
        dto.setScheduledDateTime(testDrive.getScheduledDateTime());
        dto.setActualStartTime(testDrive.getActualStartTime());
        dto.setActualEndTime(testDrive.getActualEndTime());
        dto.setStatus(testDrive.getStatus());
        dto.setCustomerFeedback(testDrive.getCustomerFeedback());
        dto.setStaffMemberName(testDrive.getStaffMemberName());
        return dto;
    }
} 