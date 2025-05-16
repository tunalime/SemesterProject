package dev.tunalime.SemesterProject.repository;

import dev.tunalime.SemesterProject.entity.Vehicle;
import dev.tunalime.SemesterProject.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    
    List<Vehicle> findByBrand(String brand);
    
    List<Vehicle> findByModel(String model);
    
    List<Vehicle> findByYear(Integer year);
    
    List<Vehicle> findByPackageType(String packageType);
    
    List<Vehicle> findByStatus(VehicleStatus status);
    
    List<Vehicle> findByStatusIn(List<VehicleStatus> statuses);
    
    List<Vehicle> findByBrandAndModel(String brand, String model);
    
    List<Vehicle> findByBrandAndModelAndYear(String brand, String model, Integer year);
    
    List<Vehicle> findByBrandAndModelAndYearAndPackageType(String brand, String model, Integer year, String packageType);
    
    List<Vehicle> findByBrandAndModelAndYearAndPackageTypeAndStatus(
            String brand, String model, Integer year, String packageType, VehicleStatus status);
    
    List<Vehicle> findByIsAvailableForTestDriveTrue();
    
    @Query("SELECT DISTINCT v.brand FROM Vehicle v ORDER BY v.brand")
    List<String> findAllDistinctBrands();
    
    @Query("SELECT DISTINCT v.model FROM Vehicle v WHERE v.brand = ?1 ORDER BY v.model")
    List<String> findAllDistinctModelsByBrand(String brand);
    
    @Query("SELECT DISTINCT v.year FROM Vehicle v WHERE v.brand = ?1 AND v.model = ?2 ORDER BY v.year DESC")
    List<Integer> findAllDistinctYearsByBrandAndModel(String brand, String model);
    
    @Query("SELECT DISTINCT v.packageType FROM Vehicle v WHERE v.brand = ?1 AND v.model = ?2 AND v.year = ?3 ORDER BY v.packageType")
    List<String> findAllDistinctPackageTypesByBrandAndModelAndYear(String brand, String model, Integer year);
    
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.brand = ?1 AND v.model = ?2 AND v.year = ?3 AND v.packageType = ?4")
    Long countByBrandAndModelAndYearAndPackageType(String brand, String model, Integer year, String packageType);
} 