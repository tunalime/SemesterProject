package dev.tunalime.SemesterProject.repository;

import dev.tunalime.SemesterProject.entity.Customer;
import dev.tunalime.SemesterProject.entity.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByLastName(String lastName);
    
    List<Customer> findByFirstNameAndLastName(String firstName, String lastName);
    
    List<Customer> findByStatus(CustomerStatus status);
    
    List<Customer> findByRegistrationDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT c FROM Customer c JOIN c.sales s GROUP BY c HAVING COUNT(s) > 0")
    List<Customer> findCustomersWithPurchases();
    
    @Query("SELECT c FROM Customer c LEFT JOIN c.sales s GROUP BY c HAVING COUNT(s) = 0")
    List<Customer> findCustomersWithoutPurchases();
    
    @Query("SELECT c FROM Customer c JOIN c.testDrives t GROUP BY c HAVING COUNT(t) > 0")
    List<Customer> findCustomersWithTestDrives();
    
    @Query("SELECT c FROM Customer c JOIN c.interactions i WHERE i.interactionDate >= ?1 GROUP BY c")
    List<Customer> findCustomersWithRecentInteractions(LocalDate sinceDate);
    
    @Query("SELECT c FROM Customer c JOIN c.sales s GROUP BY c HAVING COUNT(s) > 1")
    List<Customer> findRepeatCustomers();
} 