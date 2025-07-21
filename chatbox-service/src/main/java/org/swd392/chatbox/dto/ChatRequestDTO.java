package org.swd392.chatbox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequestDTO {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotBlank(message = "Message cannot be empty")
    private String message;

    // Thêm các trường bổ sung nếu cần
    private boolean requestAnalysis = false;

    // Có thể thêm các phương thức tạo nhanh
    public static ChatRequestDTO of(String sessionId, String message) {
        return ChatRequestDTO.builder()
                .sessionId(sessionId)
                .message(message)
                .build();
    }
}
