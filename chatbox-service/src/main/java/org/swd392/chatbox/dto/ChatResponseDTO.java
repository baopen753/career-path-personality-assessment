package org.swd392.chatbox.dto;

import org.swd392.chatbox.entity.TraitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponseDTO {
    private String sessionId;
    private String botReply;
    private String error;
    private boolean analysisAvailable;
    private String analysisPrompt;
    private Map<TraitType, Integer> updatedScores;

    public ChatResponseDTO(String error) {
        this.error = error;
    }

    public static ChatResponseDTOBuilder builder() {
        return new ChatResponseDTOBuilder();
    }

    public static class ChatResponseDTOBuilder {
        private String sessionId;
        private String botReply;
        private String error;
        private boolean analysisAvailable;
        private String analysisPrompt;
        private Map<TraitType, Integer> updatedScores;

        public ChatResponseDTOBuilder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public ChatResponseDTOBuilder botReply(String botReply) {
            this.botReply = botReply;
            return this;
        }

        public ChatResponseDTOBuilder error(String error) {
            this.error = error;
            return this;
        }

        public ChatResponseDTOBuilder analysisAvailable(boolean analysisAvailable) {
            this.analysisAvailable = analysisAvailable;
            return this;
        }

        public ChatResponseDTOBuilder analysisPrompt(String analysisPrompt) {
            this.analysisPrompt = analysisPrompt;
            return this;
        }

        public ChatResponseDTOBuilder updatedScores(Map<TraitType, Integer> updatedScores) {
            this.updatedScores = updatedScores;
            return this;
        }

        public ChatResponseDTO build() {
            ChatResponseDTO dto = new ChatResponseDTO();
            dto.setSessionId(sessionId);
            dto.setBotReply(botReply);
            dto.setError(error);
            dto.setAnalysisAvailable(analysisAvailable);
            dto.setAnalysisPrompt(analysisPrompt);
            dto.setUpdatedScores(updatedScores);
            return dto;
        }
    }
}
