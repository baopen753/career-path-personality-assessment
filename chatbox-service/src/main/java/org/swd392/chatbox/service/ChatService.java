package org.swd392.chatbox.service;

import org.swd392.chatbox.dto.ChatRequestDTO;
import org.swd392.chatbox.dto.ChatResponseDTO;
import org.swd392.chatbox.entity.ChatMessage;

import java.util.List;

public interface ChatService {

    String createNewSession(String userId);

    ChatResponseDTO processMessage(String userId, ChatRequestDTO request);

    List<ChatMessage> getChatHistory(String userId, String sessionId);

    List<ChatMessage> getChatHistory(String sessionId);

    List<String> getSessionIdsForUser(String userId);

    void deleteSession(String userId, String sessionId);
}
