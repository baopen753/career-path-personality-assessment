package org.swd392.chatbox.controller;

import org.swd392.chatbox.dto.AnalysisResultDTO;
import org.swd392.chatbox.dto.ChatRequestDTO;
import org.swd392.chatbox.dto.ChatResponseDTO;
import org.swd392.chatbox.entity.ChatMessage;
import org.swd392.chatbox.service.AnalysisService;
import org.swd392.chatbox.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final AnalysisService analysisService;

    @GetMapping("/sessions")
    public ResponseEntity<List<String>> getUserSessions(@RequestHeader("X-User-Id") String userId) {
        try {
            List<String> sessionIds = chatService.getSessionIdsForUser(userId);
            return ResponseEntity.ok(sessionIds);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách phiên chat cho user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/start")
    public ResponseEntity<?> startChat(@RequestHeader("X-User-Id") String userId) {
        try {
            log.info("Starting new chat session for user: {}", userId);
            String sessionId = chatService.createNewSession(userId);
            List<ChatMessage> messages = chatService.getChatHistory(sessionId);
            if (!messages.isEmpty()) {
                return ResponseEntity.ok(ChatResponseDTO.builder()
                        .sessionId(sessionId)
                        .botReply(messages.get(0).getContent())
                        .build());
            }
            return createErrorResponse("Không thể tạo phiên chat mới");
        } catch (IllegalStateException e) {
            // Catch the specific exception for session limit
            log.warn("User {} reached session limit", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi khi bắt đầu phiên chat mới cho user {}: {}", userId, e.getMessage(), e);
            return createErrorResponse("Lỗi khi bắt đầu phiên chat mới");
        }
    }

    @PostMapping("/message")
    public ResponseEntity<ChatResponseDTO> sendMessage(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ChatRequestDTO request) {
        try {
            // Validate
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return createErrorResponse("Vui lòng nhập tin nhắn");
            }
            if (request.getSessionId() == null || request.getSessionId().isBlank()) {
                return createErrorResponse("Session ID là bắt buộc");
            }

            log.info("Processing message for user: {} in session: {}", userId, request.getSessionId());

            // Xử lý tin nhắn và nhận phản hồi
            ChatResponseDTO response = chatService.processMessage(userId, request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Lỗi khi xử lý tin nhắn cho user {}: {}", userId, e.getMessage(), e);
            return createErrorResponse("Có lỗi xảy ra khi xử lý tin nhắn");
        }
    }

    private ResponseEntity<ChatResponseDTO> createErrorResponse(String message) {
        return ResponseEntity.badRequest()
                .body(ChatResponseDTO.builder()
                        .error(message)
                        .build());
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String sessionId) {
        try {
            log.info("Getting chat history for user: {} in session: {}", userId, sessionId);
            List<ChatMessage> messages = chatService.getChatHistory(userId, sessionId);
            if (messages.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Lỗi khi lấy lịch sử trò chuyện cho user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/analyze/{sessionId}")
    public ResponseEntity<AnalysisResultDTO> analyzeConversation(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String sessionId) {
        try {
            log.info("Analyzing conversation for user: {} in session: {}", userId, sessionId);
            AnalysisResultDTO result = analysisService.analyzeConversation(userId, sessionId);
            if (result.getError() != null) {
                return ResponseEntity.badRequest().body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi phân tích hội thoại cho user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AnalysisResultDTO("Có lỗi xảy ra khi phân tích: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String sessionId) {
        try {
            log.info("Deleting session for user: {} in session: {}", userId, sessionId);
            chatService.deleteSession(userId, sessionId);
            log.info("Đã xóa dữ liệu cho phiên: {} của user: {}", sessionId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Lỗi khi xóa phiên {} cho user {}: {}", sessionId, userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        log.error("Lỗi không xác định: {}", ex.getMessage(), ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Đã xảy ra lỗi không mong muốn");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
