package org.swd392.chatbox.service;

import org.swd392.chatbox.dto.AnalysisResultDTO;

public interface AnalysisService {
    AnalysisResultDTO analyzeConversation(String userId, String sessionId);
}
