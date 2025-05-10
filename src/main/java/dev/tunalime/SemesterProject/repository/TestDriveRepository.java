package dev.tunalime.SemesterProject.repository;

import dev.tunalime.SemesterProject.entity.TestDrive;
import dev.tunalime.SemesterProject.entity.TestDriveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestDriveRepository extends JpaRepository<TestDrive, Long> {
    
    List<TestDrive> findByCustomerId(Long customerId);
    
    List<TestDrive> findByVehicleId(Long vehicleId);
    
    List<TestDrive> findByStatus(TestDriveStatus status);
    
    List<TestDrive> findByScheduledDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    @Query("SELECT t FROM TestDrive t WHERE t.vehicle.brand = ?1")
    List<TestDrive> findByVehicleBrand(String brand);
    
    @Query("SELECT t FROM TestDrive t WHERE t.vehicle.brand = ?1 AND t.vehicle.model = ?2")
    List<TestDrive> findByVehicleBrandAndModel(String brand, String model);
    
    @Query("SELECT COUNT(t) FROM TestDrive t WHERE t.status = 'COMPLETED' AND t.customer.id = ?1")
    Long countCompletedTestDrivesByCustomer(Long customerId);
    
    @Query("SELECT COUNT(t) FROM TestDrive t WHERE t.status = 'COMPLETED' AND t.vehicle.id = ?1")
    Long countCompletedTestDrivesByVehicle(Long vehicleId);
    
    // Find test drives scheduled for today that are not completed
    @Query("SELECT t FROM TestDrive t WHERE t.scheduledDateTime BETWEEN ?1 AND ?2 AND t.status = 'SCHEDULED'")
    List<TestDrive> findScheduledTestDrivesForToday(LocalDateTime startOfDay, LocalDateTime endOfDay);
} 