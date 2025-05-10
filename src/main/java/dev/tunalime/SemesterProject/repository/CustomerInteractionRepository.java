package dev.tunalime.SemesterProject.repository;

import dev.tunalime.SemesterProject.entity.CustomerInteraction;
import dev.tunalime.SemesterProject.entity.InteractionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerInteractionRepository extends JpaRepository<CustomerInteraction, Long> {
    
    List<CustomerInteraction> findByCustomerId(Long customerId);
    
    List<CustomerInteraction> findByType(InteractionType type);
    
    List<CustomerInteraction> findByInteractionDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    @Query("SELECT i FROM CustomerInteraction i WHERE i.customer.id = ?1 ORDER BY i.interactionDate DESC")
    List<CustomerInteraction> findByCustomerIdOrderByInteractionDateDesc(Long customerId);
    
    @Query("SELECT i FROM CustomerInteraction i WHERE i.vehicle.id = ?1")
    List<CustomerInteraction> findByVehicleId(Long vehicleId);
    
    @Query("SELECT COUNT(i) FROM CustomerInteraction i WHERE i.type = ?1")
    Long countByInteractionType(InteractionType type);
    
    @Query("SELECT i.type, COUNT(i) FROM CustomerInteraction i GROUP BY i.type")
    List<Object[]> countInteractionsByType();
    
    // Count interactions per day for a date range
    @Query("SELECT FUNCTION('DATE', i.interactionDate) as date, COUNT(i) FROM CustomerInteraction i WHERE i.interactionDate BETWEEN ?1 AND ?2 GROUP BY FUNCTION('DATE', i.interactionDate) ORDER BY date")
    List<Object[]> countInteractionsByDateForRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
} 