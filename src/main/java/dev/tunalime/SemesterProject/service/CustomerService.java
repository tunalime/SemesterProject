package dev.tunalime.SemesterProject.service;

import dev.tunalime.SemesterProject.dto.CustomerDTO;
import dev.tunalime.SemesterProject.entity.Customer;
import dev.tunalime.SemesterProject.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import jakarta.persistence.criteria.Predicate;

/**
 * Service for customer operations
 */
@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    /**
     * Get all customers
     * 
     * @return List of all customers
     */
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get customer by ID
     * 
     * @param id Customer ID
     * @return Customer DTO
     */
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        return convertToDTO(customer);
    }
    
    /**
     * Get customer by email
     * 
     * @param email Customer email
     * @return Customer DTO or null if not found
     */
    public CustomerDTO getCustomerByEmail(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        return customer.map(this::convertToDTO).orElse(null);
    }
    
    /**
     * Add a new customer
     * 
     * @param customerDTO Customer information
     * @return Added customer
     */
    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        // Check if email already exists
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customerDTO.getEmail());
        if (existingCustomer.isPresent()) {
            throw new RuntimeException("Customer with email " + customerDTO.getEmail() + " already exists");
        }
        
        Customer customer = new Customer();
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setAddress(customerDTO.getAddress());
        customer.setRegistrationDate(LocalDate.now());
        
        customer = customerRepository.save(customer);
        
        return convertToDTO(customer);
    }
    
    /**
     * Update customer information
     * 
     * @param id Customer ID
     * @param customerDTO Updated customer information
     * @return Updated customer
     */
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        
        // Check email uniqueness if changed
        if (!customer.getEmail().equals(customerDTO.getEmail())) {
            Optional<Customer> existingCustomer = customerRepository.findByEmail(customerDTO.getEmail());
            if (existingCustomer.isPresent() && !existingCustomer.get().getId().equals(id)) {
                throw new RuntimeException("Customer with email " + customerDTO.getEmail() + " already exists");
            }
        }
        
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setAddress(customerDTO.getAddress());
        
        customer = customerRepository.save(customer);
        
        return convertToDTO(customer);
    }
    
    /**
     * Delete a customer
     * 
     * @param id Customer ID
     */
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        
        customerRepository.delete(customer);
    }
    
    /**
     * Search customers by name
     * 
     * @param firstName First name (optional)
     * @param lastName Last name (optional)
     * @return List of matching customers
     */
    public List<CustomerDTO> searchCustomersByName(String firstName, String lastName) {
        List<Customer> customers;
        
        // Use dynamic query with Specification API
        customers = customerRepository.findAll((root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            
            if (firstName != null && !firstName.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("firstName"), firstName));
            }
            
            if (lastName != null && !lastName.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("lastName"), lastName));
            }
            
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        });
        
        return customers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get customers who have made purchases
     * 
     * @return List of customers who have made purchases
     */
    public List<CustomerDTO> getCustomersWithPurchases() {
        return customerRepository.findCustomersWithPurchases().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get repeat customers (who have made multiple purchases)
     * 
     * @return List of repeat customers
     */
    public List<CustomerDTO> getRepeatCustomers() {
        return customerRepository.findRepeatCustomers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get customers registered between two dates
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of customers registered between the specified dates
     */
    public List<CustomerDTO> getCustomersRegisteredBetween(LocalDate startDate, LocalDate endDate) {
        return customerRepository.findByRegistrationDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Advanced search for customers by multiple criteria
     * 
     * @param firstName First name (optional)
     * @param lastName Last name (optional)
     * @param email Email (optional)
     * @param phone Phone (optional)
     * @return List of matching customers
     */
    public List<CustomerDTO> advancedSearch(String firstName, String lastName, String email, 
                                           String phone) {
        List<Customer> customers = customerRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (firstName != null && !firstName.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("firstName"), firstName));
            }
            
            if (lastName != null && !lastName.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("lastName"), lastName));
            }
            
            if (email != null && !email.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("email"), email));
            }
            
            if (phone != null && !phone.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("phone"), phone));
            }
            
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        });
        
        return customers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Customer entity to DTO
     * 
     * @param customer Customer entity
     * @return Customer DTO
     */
    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setRegistrationDate(customer.getRegistrationDate());
        return dto;
    }
} 