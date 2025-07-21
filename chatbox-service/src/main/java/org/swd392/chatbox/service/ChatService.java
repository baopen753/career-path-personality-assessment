package org.swd392.chatbox.service;

import org.swd392.chatbox.dto.ChatRequestDTO;
import org.swd392.chatbox.dto.ChatResponseDTO;
import org.swd392.chatbox.entity.ChatMessage;
import org.swd392.chatbox.repository.AnalysisResultRepository;
import org.swd392.chatbox.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int MIN_MESSAGES_FOR_ANALYSIS = 5;
    private static final int MAX_SESSIONS_PER_USER = 10;

    private final ChatMessageRepository chatMessageRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final GeminiResponseService geminiService;

    @Transactional
    public String createNewSession(String userId) {
        List<String> userSessions = chatMessageRepository.findSessionIdsByUserId(userId);
        if (userSessions.size() >= MAX_SESSIONS_PER_USER) {
            log.warn("User {} has reached the session limit of {}", userId, MAX_SESSIONS_PER_USER);
            throw new IllegalStateException("You have reached the maximum number of " + MAX_SESSIONS_PER_USER + " sessions. Please delete an old one to create a new chat.");
        }

        String sessionId = "chat-" + UUID.randomUUID().toString();
        ChatMessage welcomeMessage = ChatMessage.builder()
                .userId(userId)
                .sessionId(sessionId)
                .sender("assistant")
                .content("Xin chào! Tôi là trợ lý chuyên về tư vấn hướng nghiệp và giáo dục. Tôi có thể giúp bạn tìm hiểu về tính cách MBTI/DISC, khám phá các ngành học phù hợp, hoặc cung cấp thông tin về các trường đại học tại TP.HCM. Bạn cần tôi giúp gì hôm nay?")
                .build();
        chatMessageRepository.save(welcomeMessage);
        log.info("Đã tạo phiên chat mới: {} cho user: {}", sessionId, userId);
        return sessionId;
    }

    @Transactional
    public ChatResponseDTO processMessage(String userId, ChatRequestDTO request) {
        if (!chatMessageRepository.existsByUserIdAndSessionId(userId, request.getSessionId())) {
            throw new SecurityException("Bạn không có quyền truy cập vào session này.");
        }

        saveMessage(userId, request.getSessionId(), "user", request.getMessage());
        String botReply = generateBotResponse(request.getSessionId());
        saveMessage(userId, request.getSessionId(), "assistant", botReply);

        boolean analysisAvailable = shouldOfferAnalysis(request.getSessionId());

        return ChatResponseDTO.builder()
                .sessionId(request.getSessionId())
                .botReply(botReply)
                .analysisAvailable(analysisAvailable)
                .analysisPrompt(analysisAvailable ? "Cuộc trò chuyện của chúng ta đã đủ sâu. Bạn có muốn tôi thực hiện một phân tích tính cách chi tiết ngay bây giờ không?" : "")
                .build();
    }

    private String generateBotResponse(String sessionId) {
        List<ChatMessage> history = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        String context = history.stream()
                .map(m -> m.getSender() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        String systemPrompt = """
            Bạn là một trợ lý AI chuyên sâu về tư vấn hướng nghiệp và giáo dục tại Việt Nam. Vai trò của bạn là cung cấp thông tin chính xác, cập nhật và hữu ích.

            QUY TẮC BẮT BUỘC:
            1.  **Chuyên môn:** Chỉ thảo luận về:
                * Phân tích tính cách (MBTI, DISC) và gợi ý nghề nghiệp tương ứng.
                * Thông tin về các trường đại học tại TP. Hồ Chí Minh (ngành học, học phí, điểm chuẩn).
                * Xu hướng ngành nghề cho năm 2025 tại Việt Nam.
            2.  **Ngôn ngữ:** Bắt buộc sử dụng 100% tiếng Việt, với giọng văn thân thiện, chuyên nghiệp.
            3.  **Giới hạn:** Nếu người dùng hỏi về chủ đề ngoài chuyên môn (ví dụ: thời tiết, nấu ăn, chính trị), bạn BẮT BUỘC phải trả lời bằng duy nhất câu sau: "Xin lỗi, tôi không có khả năng trả lời câu hỏi này." Không thêm bất cứ giải thích nào khác.

            Dựa vào các quy tắc trên và lịch sử trò chuyện, hãy trả lời tin nhắn mới nhất của người dùng.
            """;

        String finalPrompt = String.format("""
            %s

            Lịch sử trò chuyện:
            %s

            Hãy trả lời câu hỏi cuối cùng của người dùng theo đúng vai trò và quy tắc của bạn.
            """, systemPrompt, context);

        return geminiService.generateResponse(finalPrompt);
    }

    private void saveMessage(String userId, String sessionId, String sender, String content) {
        ChatMessage message = ChatMessage.builder()
                .userId(userId)
                .sessionId(sessionId)
                .sender(sender)
                .content(content)
                .build();
        chatMessageRepository.save(message);
    }

    private boolean shouldOfferAnalysis(String sessionId) {
        if (analysisResultRepository.existsBySessionId(sessionId)) {
            return false;
        }
        long userMessageCount = chatMessageRepository.countBySessionIdAndSender(sessionId, "user");
        return userMessageCount >= MIN_MESSAGES_FOR_ANALYSIS;
    }

    public List<ChatMessage> getChatHistory(String userId, String sessionId) {
        if (!chatMessageRepository.existsByUserIdAndSessionId(userId, sessionId)) {
            throw new SecurityException("Bạn không có quyền truy cập vào session này.");
        }
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    public List<ChatMessage> getChatHistory(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    public List<String> getSessionIdsForUser(String userId) {
        log.info("Fetching session IDs for user: {}", userId);
        return chatMessageRepository.findSessionIdsByUserId(userId);
    }

    @Transactional
    public void deleteSession(String userId, String sessionId) {
        // Kiểm tra quyền sở hữu trước khi xóa
        if (!chatMessageRepository.existsByUserIdAndSessionId(userId, sessionId)) {
            throw new SecurityException("Bạn không có quyền xóa session này.");
        }

        chatMessageRepository.deleteBySessionId(sessionId);
        analysisResultRepository.deleteBySessionId(sessionId);
        log.info("Đã xóa toàn bộ dữ liệu cho phiên: {} của user: {}", sessionId, userId);
    }
}
