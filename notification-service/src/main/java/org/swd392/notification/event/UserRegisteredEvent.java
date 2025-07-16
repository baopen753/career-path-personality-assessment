package org.swd392.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private String email;
    private String accountType;
    private LocalDateTime registrationDate;
    private String loginLink;
}
