package dev.tunalime.SemesterProject.dto;

import dev.tunalime.SemesterProject.entity.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Customer entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private LocalDate registrationDate;
    private CustomerStatus status;
    private String notes;
} 