package org.swd392.seminars.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.swd392.seminars.validation.ValidSeminarTime;
import java.time.LocalDateTime;

@Data
@ValidSeminarTime
public class SeminarRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 1, max = 1000, message = "Description must be between 1 and 1000 characters")
    private String description;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be greater than 0")
    @Max(value = 480, message = "Duration cannot exceed 480 minutes (8 hours)")
    private Integer duration;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private Double price;
    
    @Pattern(regexp = "^https?://.*", message = "Meeting URL must be a valid URL")
    private String meetingUrl;
    
    @Pattern(regexp = "^https?://.*", message = "Form URL must be a valid URL")
    private String formUrl;
    
    @NotNull(message = "Slot is required")
    @Min(value = 1, message = "Slot must be greater than 0")
    @Max(value = 1000, message = "Slot cannot exceed 1000")
    private Integer slot;
    
    @Pattern(regexp = "^https?://.*", message = "Image URL must be a valid URL")
    private String imageUrl;
    
    @NotNull(message = "Starting time is required")
    @Future(message = "Starting time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startingTime;
    
    @NotNull(message = "Ending time is required")
    @Future(message = "Ending time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endingTime;
}
