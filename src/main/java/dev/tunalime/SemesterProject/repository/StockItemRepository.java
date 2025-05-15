package dev.tunalime.SemesterProject.repository;

import dev.tunalime.SemesterProject.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem, Long> {
    
    List<StockItem> findByBrand(String brand);
    
    List<StockItem> findByModel(String model);
    
    List<StockItem> findByYear(Integer year);
    
    List<StockItem> findByPackageType(String packageType);
    
    Optional<StockItem> findByBrandAndModelAndYearAndPackageType(
            String brand, String model, Integer year, String packageType);
    
    @Query("SELECT s FROM StockItem s JOIN s.vehicles v WHERE v.status = 'IN_STOCK' OR v.status = 'IN_SHOWROOM' GROUP BY s")
    List<StockItem> findAllWithAvailableVehicles();
    
    @Query("SELECT DISTINCT s.brand FROM StockItem s ORDER BY s.brand")
    List<String> findAllDistinctBrands();
    
    @Query("SELECT DISTINCT s.model FROM StockItem s WHERE s.brand = ?1 ORDER BY s.model")
    List<String> findAllDistinctModelsByBrand(String brand);
    
    @Query("SELECT DISTINCT s.year FROM StockItem s WHERE s.brand = ?1 AND s.model = ?2 ORDER BY s.year DESC")
    List<Integer> findAllDistinctYearsByBrandAndModel(String brand, String model);
    
    @Query("SELECT DISTINCT s.packageType FROM StockItem s WHERE s.brand = ?1 AND s.model = ?2 AND s.year = ?3 ORDER BY s.packageType")
    List<String> findAllDistinctPackageTypesByBrandAndModelAndYear(String brand, String model, Integer year);
} 