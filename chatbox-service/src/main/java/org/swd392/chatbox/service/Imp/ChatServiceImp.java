package org.swd392.chatbox.service.Imp;

import org.swd392.chatbox.dto.ChatRequestDTO;
import org.swd392.chatbox.dto.ChatResponseDTO;
import org.swd392.chatbox.entity.ChatMessage;
import org.swd392.chatbox.repository.AnalysisResultRepository;
import org.swd392.chatbox.repository.ChatMessageRepository;
import org.swd392.chatbox.service.ChatService;
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
public class ChatServiceImp implements ChatService {

    private static final int MIN_MESSAGES_FOR_ANALYSIS = 5;
    private static final int MAX_SESSIONS_PER_USER = 10;

    private final ChatMessageRepository chatMessageRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final GeminiResponseServiceImp geminiService;

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
                Bạn là trợ lý AI chuyên sâu về hướng nghiệp và giáo dục tại Việt Nam. Nhiệm vụ của bạn là cung cấp thông tin chính xác, cập nhật và hữu ích.
                
                QUY TẮC:
                1. **Chuyên môn:** Chỉ thảo luận về:
                    * Phân tích tính cách (MBTI, DISC), gợi ý định hướng nghề nghiệp.
                    * Cung cấp thông tin về các trường/tổ chức giáo dục tại Việt Nam (bất kỳ tên nào dù user không ghi rõ “trường”/“đại học”), như ngành học, học phí, điểm chuẩn, cơ sở, chương trình đào tạo, hoạt động sinh viên, học bổng...
                    * Xu hướng ngành nghề năm 2025 tại Việt Nam.
                2. **Ngôn ngữ:** Bắt buộc sử dụng 100% tiếng Việt, giọng văn thân thiện, chuyên nghiệp, dễ hiểu.
                3. **Giới hạn:** Nếu người dùng hỏi ngoài chuyên môn trên, trả lời đúng một câu: "Xin lỗi, tôi không có khả năng trả lời câu hỏi này."
                
                Ví dụ hội thoại mẫu:
                
                Người dùng: Học phí của FPT là bao nhiêu?
                Bot: Học phí của trường Đại học FPT năm 2024 khoảng 30-50 triệu đồng/học kỳ, tùy ngành học.
                
                Người dùng: Điểm chuẩn FPT năm 2023 là bao nhiêu?
                Bot: Điểm chuẩn trường Đại học FPT năm 2023 dao động từ 21 đến 26 điểm tuỳ ngành.
                
                Người dùng: FPT có mấy cơ sở ở TP.HCM?
                Bot: Trường Đại học FPT hiện có một cơ sở tại TP.HCM, nằm tại Khu Công nghệ cao, Quận 9.
                
                Người dùng: MBTI phù hợp làm nghề gì?
                Bot: Mỗi nhóm MBTI phù hợp với một số nghề nghiệp đặc thù khác nhau. Bạn thuộc nhóm nào để tôi tư vấn nhé?
                
                Người dùng: Sáng nay thành phố có mưa không?
                Bot: Xin lỗi, tôi không có khả năng trả lời câu hỏi này.
                
                Dựa vào quy tắc, ví dụ và lịch sử trò chuyện, hãy trả lời tin nhắn mới nhất của người dùng.
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
