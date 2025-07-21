package org.swd392.chatbox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO chứa kết quả phân tích tính cách
 * Bao gồm cả thông tin MBTI và DISC
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultDTO {
    // Thông tin MBTI
    private String mbtiType;

    // Thông tin DISC
    private String discType;

    // Điểm số các yếu tố tính cách
    private List<TraitDetailDTO> traits;

    // Các đặc điểm tính cách chính
    private List<String> keyTraits;

    // Các ngành nghề phù hợp
    private List<String> suitableCareers;

    // Điểm mạnh
    private List<String> strengths;

    // Điểm yếu
    private List<String> weaknesses;

    // Phân tích chi tiết
    private String analysis;

    // Gợi ý phát triển bản thân
    private String developmentSuggestions;

    // Thời gian phân tích
    private LocalDateTime analyzedAt;

    // Thông báo lỗi (nếu có)
    private String error;

    /**
     * Lớp chứa điểm số các yếu tố tính cách
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TraitDetailDTO {
        private String name;
        private int score;
        private String description;
    }

    /**
     * Constructor cho trường hợp lỗi
     */
    public AnalysisResultDTO(String error) {
        this.error = error;
        this.analyzedAt = LocalDateTime.now();
    }

    /**
     * @deprecated Chỉ để tương thích ngược, sử dụng getMbtiType() thay thế
     */
    @Deprecated
    public String getPersonalityType() {
        return this.mbtiType;
    }

    /**
     * @deprecated Chỉ để tương thích ngược, sử dụng getDevelopmentSuggestions() thay thế
     */
    @Deprecated
    public String getSuggestions() {
        return this.developmentSuggestions;
    }
}
