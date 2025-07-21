package org.swd392.seminars.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarApprovedEvent {
    private Integer seminarId;
    private String seminarTitle;
    private String managerEmail;
    private String managerFullName;
    private String approvedAt;
    private String statusApprove;
} 