package dev.tunalime.SemesterProject.dto;

import dev.tunalime.SemesterProject.entity.TestDriveStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for TestDrive entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDriveDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long vehicleId;
    private String vehicleInfo;
    private LocalDateTime scheduledDateTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private TestDriveStatus status;
    private String customerFeedback;
    private String staffMemberName;
} 