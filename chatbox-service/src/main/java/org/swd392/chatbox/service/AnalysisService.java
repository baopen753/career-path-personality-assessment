package org.swd392.chatbox.service;


import com.google.gson.JsonElement;
import org.swd392.chatbox.dto.AnalysisResultDTO;
import org.swd392.chatbox.entity.AnalysisResult;
import org.swd392.chatbox.entity.ChatMessage;
import org.swd392.chatbox.repository.AnalysisResultRepository;
import org.swd392.chatbox.repository.ChatMessageRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ChatMessageRepository chatMessageRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final GeminiResponseService geminiService;
    private final Gson gson = new Gson();

    @Transactional
    public AnalysisResultDTO analyzeConversation(String userId, String sessionId) {
        // Kiểm tra quyền sở hữu session
        if (!chatMessageRepository.existsByUserIdAndSessionId(userId, sessionId)) {
            throw new SecurityException("Bạn không có quyền phân tích session này.");
        }

        // Sử dụng phương thức mới để lấy kết quả phân tích an toàn hơn
        Optional<AnalysisResult> existingResult = analysisResultRepository.findByUserIdAndSessionId(userId, sessionId);
        if (existingResult.isPresent()) {
            log.info("Đã tìm thấy kết quả phân tích có sẵn cho session: {} của user: {}", sessionId, userId);
            return convertToDTO(existingResult.get());
        }
        return performNewAnalysis(userId, sessionId);
    }

    private AnalysisResultDTO performNewAnalysis(String userId, String sessionId) {
        List<ChatMessage> messages = chatMessageRepository.findUserMessagesBySessionId(sessionId);
        if (messages.size() < 3) {
            return new AnalysisResultDTO("Không đủ dữ liệu để phân tích. Cần ít nhất 3 tin nhắn từ người dùng.");
        }

        // Verify all messages belong to the same user
        boolean allMessagesFromSameUser = messages.stream()
                .allMatch(msg -> userId.equals(msg.getUserId()));

        if (!allMessagesFromSameUser) {
            throw new IllegalArgumentException("Có tin nhắn không thuộc về user này trong session");
        }

        String conversation = messages.stream()
                .map(ChatMessage::getContent)
                .collect(Collectors.joining("\n---\n"));

        String prompt = String.format("""
            DỰA VÀO ĐOẠN HỘI THOẠI SAU ĐÂY:
            ---
            %s
            ---
            HÃY THỰC HIỆN CÁC YÊU CẦU PHÂN TÍCH SAU:
            1.  **Xác định loại MBTI**: (ví dụ: INTJ, ENFP).
            2.  **Xác định loại DISC nổi bật**: (ví dụ: D - Dominance, I - Influence, S - Steadiness, C - Compliance).
            3.  **Đánh giá điểm số (1-10)** cho từng yếu tố trong một object "traits".
            4.  **Liệt kê 4-5 đặc điểm chính (keyTraits)** dưới dạng một mảng chuỗi.
            5.  **Gợi ý 5 ngành nghề phù hợp (suitableCareers)** dưới dạng một mảng chuỗi.
            6.  **Liệt kê 3 điểm mạnh (strengths)** và **3 điểm yếu (weaknesses)**, mỗi loại là một mảng chuỗi.
            7.  **Viết một đoạn phân tích chi tiết (analysis)** về tính cách này.
            8.  **Đưa ra 2-3 gợi ý phát triển bản thân (developmentSuggestions)**.

            TRẢ VỀ KẾT QUẢ DƯỚI ĐỊNH DẠNG JSON CHÍNH XÁC NHƯ SAU, KHÔNG THÊM BẤT KỲ GIẢI THÍCH NÀO:
            {
                "mbtiType": "...",
                "discType": "...",
                "traits": {
                    "extraversion": 0, "intuition": 0, "thinking": 0, "judging": 0,
                    "dominance": 0, "influence": 0, "steadiness": 0, "compliance": 0
                },
                "keyTraits": ["...", "..."],
                "suitableCareers": ["...", "..."],
                "strengths": ["...", "..."],
                "weaknesses": ["...", "..."],
                "analysis": "...",
                "developmentSuggestions": "..."
            }
            """, conversation);

        String geminiResponse = geminiService.generateResponse(prompt);
        AnalysisResult result = saveAnalysisResult(userId, sessionId, geminiResponse);
        return convertToDTO(result);
    }

    private AnalysisResult saveAnalysisResult(String userId, String sessionId, String geminiResponse) {
        try {
            String jsonString = geminiResponse.substring(geminiResponse.indexOf('{'), geminiResponse.lastIndexOf('}') + 1);
            JsonObject jsonResponse = JsonParser.parseString(jsonString).getAsJsonObject();
            JsonObject traits = jsonResponse.getAsJsonObject("traits");

            AnalysisResult result = AnalysisResult.builder()
                    .userId(userId)
                    .sessionId(sessionId)
                    // APPLYING THE FIX to all string fields
                    .mbtiType(getAsStringOrJoinedArray(jsonResponse.get("mbtiType")))
                    .discType(getAsStringOrJoinedArray(jsonResponse.get("discType")))
                    .extraversion(traits.get("extraversion").getAsInt())
                    .intuition(traits.get("intuition").getAsInt())
                    .thinking(traits.get("thinking").getAsInt())
                    .judging(traits.get("judging").getAsInt())
                    .dominance(traits.get("dominance").getAsInt())
                    .influence(traits.get("influence").getAsInt())
                    .steadiness(traits.get("steadiness").getAsInt())
                    .compliance(traits.get("compliance").getAsInt())
                    .keyTraits(gson.toJson(jsonResponse.get("keyTraits")))
                    .suitableCareers(gson.toJson(jsonResponse.get("suitableCareers")))
                    .strengths(gson.toJson(jsonResponse.get("strengths")))
                    .weaknesses(gson.toJson(jsonResponse.get("weaknesses")))
                    // APPLYING THE FIX to all string fields
                    .analysis(getAsStringOrJoinedArray(jsonResponse.get("analysis")))
                    .developmentSuggestions(getAsStringOrJoinedArray(jsonResponse.get("developmentSuggestions")))
                    .build();

            AnalysisResult savedResult = analysisResultRepository.save(result);
            log.info("Đã lưu kết quả phân tích cho user: {} trong session: {}", userId, sessionId);
            return savedResult;
        } catch (Exception e) {
            log.error("Lỗi nghiêm trọng khi parse JSON hoặc lưu vào DB cho user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Định dạng dữ liệu trả về từ AI không hợp lệ.", e);
        }
    }

    private AnalysisResultDTO convertToDTO(AnalysisResult result) {
        Type listType = new TypeToken<List<String>>() {}.getType();

        // ▼▼▼ START OF CHANGES ▼▼▼

        // Create the list of trait details
        List<AnalysisResultDTO.TraitDetailDTO> traitDetails = new ArrayList<>();
        traitDetails.add(createTraitDetail("Extraversion", result.getExtraversion(), "Reflects how you interact with others and where you get your energy."));
        traitDetails.add(createTraitDetail("Intuition", result.getIntuition(), "Describes how you perceive information and what you naturally notice."));
        traitDetails.add(createTraitDetail("Thinking", result.getThinking(), "Indicates your basis for making decisions and judgments."));
        traitDetails.add(createTraitDetail("Judging", result.getJudging(), "Shows your preference for structure and planning in the outer world."));
        traitDetails.add(createTraitDetail("Dominance", result.getDominance(), "Measures how you handle problems and challenges."));
        traitDetails.add(createTraitDetail("Influence", result.getInfluence(), "Pertains to your ability to persuade and interact with people."));
        traitDetails.add(createTraitDetail("Steadiness", result.getSteadiness(), "Relates to your pace, patience, and thoughtfulness."));
        traitDetails.add(createTraitDetail("Compliance", result.getCompliance(), "Concerns how you approach rules and procedures set by others."));

        return AnalysisResultDTO.builder()
                .mbtiType(result.getMbtiType())
                .discType(result.getDiscType())
                .traits(traitDetails) // Use the new list of detailed traits
                .keyTraits(gson.fromJson(result.getKeyTraits(), listType))
                .suitableCareers(gson.fromJson(result.getSuitableCareers(), listType))
                .strengths(gson.fromJson(result.getStrengths(), listType))
                .weaknesses(gson.fromJson(result.getWeaknesses(), listType))
                .analysis(result.getAnalysis())
                .developmentSuggestions(result.getDevelopmentSuggestions())
                .analyzedAt(result.getUpdatedAt())
                .build();
    }

    private AnalysisResultDTO.TraitDetailDTO createTraitDetail(String name, Integer score, String description) {
        return AnalysisResultDTO.TraitDetailDTO.builder()
                .name(name)
                .score(score != null ? score : 0) // Handle potential null scores
                .description(description)
                .build();
    }

    private String getAsStringOrJoinedArray(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return "";
        }
        if (element.isJsonArray()) {
            // Convert array elements to a single string, separated by a comma and space
            return StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                    .map(JsonElement::getAsString)
                    .collect(Collectors.joining(", "));
        }
        return element.getAsString();
    }
}
