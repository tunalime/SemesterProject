package dev.tunalime.SemesterProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a test drive
 */
@Entity
@Table(name = "test_drives")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDrive {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Column(nullable = false)
    private LocalDateTime scheduledDateTime;
    
    private LocalDateTime actualStartTime;
    
    private LocalDateTime actualEndTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestDriveStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String customerFeedback;
    
    // Staff member who accompanied the test drive
    private String staffMemberName;
} 