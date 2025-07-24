package org.swd392.users.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDto {

    private Long userId;
    private String packageType;
    private LocalDateTime createdAt;

}
