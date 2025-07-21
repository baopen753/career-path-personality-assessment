package org.swd392.chatbox.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.swd392.chatbox.entity.TraitType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class GeminiResponseService {

    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 20000;
    private static final int MAX_PROMPT_LENGTH = 15000; // Character limit for prompts
    private static final int MAX_OUTPUT_TOKENS = 4000; // Increased token limit

    // Enhanced conversation context tracking
    private final Map<String, ConversationContext> sessionContexts = new HashMap<>();

    // Personality indicators for better analysis
    private static final Map<String, Set<String>> PERSONALITY_INDICATORS = Map.of(
            "EXTRAVERSION", Set.of("nhóm", "gặp mặt", "thích nói chuyện", "hoạt động xã hội", "năng động"),
            "INTROVERSION", Set.of("một mình", "yên tĩnh", "suy nghĩ", "tập trung", "độc lập"),
            "INTUITION", Set.of("ý tưởng", "khả năng", "tương lai", "sáng tạo", "trừu tượng"),
            "SENSING", Set.of("thực tế", "chi tiết", "kinh nghiệm", "cụ thể", "hiện tại"),
            "THINKING", Set.of("logic", "phân tích", "lý do", "khách quan", "công bằng"),
            "FEELING", Set.of("cảm xúc", "con người", "hài hòa", "giá trị", "đồng cảm")
    );

    public GeminiResponseService(WebClient geminiWebClient) {
        this.geminiWebClient = geminiWebClient;
        this.objectMapper = new ObjectMapper();
    }

    // Inner class for conversation context
    public static class ConversationContext {
        private final List<String> topics = new ArrayList<>();
        private final Map<String, Integer> keywordCounts = new HashMap<>();
        private int messageCount = 0;
        private String dominantTone = "neutral";

        public void addMessage(String message) {
            messageCount++;
            String lowerMessage = message.toLowerCase();

            // Track personality-related keywords
            for (Map.Entry<String, Set<String>> entry : PERSONALITY_INDICATORS.entrySet()) {
                for (String keyword : entry.getValue()) {
                    if (lowerMessage.contains(keyword)) {
                        keywordCounts.merge(entry.getKey(), 1, Integer::sum);
                    }
                }
            }

            // Extract topics (simplified)
            if (lowerMessage.contains("nghề") || lowerMessage.contains("công việc")) {
                topics.add("career");
            }
            if (lowerMessage.contains("học") || lowerMessage.contains("trường")) {
                topics.add("education");
            }
        }

        public Map<String, Integer> getKeywordCounts() { return keywordCounts; }
        public List<String> getTopics() { return topics; }
        public int getMessageCount() { return messageCount; }
    }

    /**
     * Safe JSON request body creation using ObjectMapper
     */
    private String createSafeRequestBody(String prompt) {
        try {
            // Truncate prompt if it's too long
            String truncatedPrompt = truncatePrompt(prompt, MAX_PROMPT_LENGTH);

            ObjectNode requestNode = objectMapper.createObjectNode();
            ObjectNode contentNode = objectMapper.createObjectNode();
            ObjectNode partNode = objectMapper.createObjectNode();
            ObjectNode configNode = objectMapper.createObjectNode();

            // Set the text content safely
            partNode.put("text", truncatedPrompt);

            // Build the structure
            contentNode.set("parts", objectMapper.createArrayNode().add(partNode));
            requestNode.set("contents", objectMapper.createArrayNode().add(contentNode));

            // Set generation config
            configNode.put("temperature", 0.7);
            configNode.put("maxOutputTokens", MAX_OUTPUT_TOKENS);
            configNode.put("topP", 0.8);
            configNode.put("topK", 40);

            requestNode.set("generationConfig", configNode);

            return objectMapper.writeValueAsString(requestNode);

        } catch (Exception e) {
            log.error("Error creating request body", e);
            throw new RuntimeException("Cannot create safe request body", e);
        }
    }

    /**
     * Truncate prompt to stay within limits while preserving important content
     */
    private String truncatePrompt(String prompt, int maxLength) {
        if (prompt.length() <= maxLength) {
            return prompt;
        }

        log.warn("Prompt too long ({}), truncating to {} characters", prompt.length(), maxLength);

        // Try to find a good truncation point
        String truncated = prompt.substring(0, maxLength);
        int lastNewline = truncated.lastIndexOf('\n');
        int lastPeriod = truncated.lastIndexOf('.');

        // Use the last complete sentence or paragraph if possible
        if (lastNewline > maxLength * 0.8) {
            return prompt.substring(0, lastNewline) + "\n[Nội dung đã được rút gọn]";
        } else if (lastPeriod > maxLength * 0.8) {
            return prompt.substring(0, lastPeriod + 1) + " [Nội dung đã được rút gọn]";
        }

        return truncated + "... [Nội dung đã được rút gọn]";
    }

    /**
     * Enhanced response generation with better error handling
     */
    public String generateContextualResponse(String sessionId, String prompt, String userMessage) {
        try {
            // Update conversation context
            ConversationContext context = sessionContexts.computeIfAbsent(sessionId, k -> new ConversationContext());
            context.addMessage(userMessage);

            // Enhance prompt with context (but limit size)
            String enhancedPrompt = buildEnhancedPrompt(prompt, context);

            return generateResponseWithRetry(enhancedPrompt, 0);
        } catch (Exception e) {
            log.error("Error in contextual response generation for session {}", sessionId, e);
            // Fallback to simple response without context
            try {
                return generateResponseWithRetry(prompt, 0);
            } catch (Exception fallbackError) {
                log.error("Fallback response generation also failed", fallbackError);
                return "Xin lỗi, hiện tại tôi gặp khó khăn trong việc phản hồi. Bạn có thể thử lại sau không?";
            }
        }
    }

    private String buildEnhancedPrompt(String originalPrompt, ConversationContext context) {
        StringBuilder enhancement = new StringBuilder(originalPrompt);

        // Add context information but keep it concise
        if (context.getMessageCount() > 3) {
            enhancement.append("\n\nBỐI CẢNH (Ngắn gọn):\n");
            enhancement.append("- Tin nhắn: ").append(context.getMessageCount()).append("\n");

            // Add only top 2 personality indicators
            if (!context.getKeywordCounts().isEmpty()) {
                enhancement.append("- Xu hướng: ");
                context.getKeywordCounts().entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .limit(2)
                        .forEach(entry -> enhancement.append(entry.getKey()).append(", "));
                enhancement.append("\n");
            }
        }

        return enhancement.toString();
    }

    /**
     * Enhanced trait extraction with better error handling
     */
    public Map<TraitType, Integer> extractTraitScoresWithConfidence(String userMessage, String sessionId) {
        try {
            ConversationContext context = sessionContexts.get(sessionId);

            // Create a more concise prompt to avoid token limits
            String enhancedPrompt = String.format("""
                Phân tích tính cách dựa trên tin nhắn sau.
                
                PHƯƠNG PHÁP:
                1. Đánh giá nội dung và cách diễn đạt
                2. Xem xét độ chi tiết vs. tổng quát
                3. Phân tích khuynh hướng ra quyết định
                
                Trả về JSON với điểm 0-3:
                {"E":0,"I":0,"N":0,"S":0,"T":0,"F":0,"J":0,"P":0,"D_DISC":0,"I_DISC":0,"S_DISC":0,"C_DISC":0}
                
                %s
                
                Tin nhắn: "%s"
                
                CHỈ JSON, không giải thích.
                """,
                    context != null ? buildContextSummary(context) : "",
                    sanitizeText(userMessage));

            String response = generateResponse(enhancedPrompt);
            return parseTraitScores(response);
        } catch (Exception e) {
            log.error("Error extracting trait scores for session {}", sessionId, e);
            return getAdaptiveDefaultScores(userMessage);
        }
    }

    /**
     * Better text sanitization
     */
    private String sanitizeText(String text) {
        if (text == null) return "";

        return text
                .trim()
                .replaceAll("[\r\n]+", " ")  // Replace newlines with spaces
                .replaceAll("\\s+", " ")     // Normalize whitespace
                .substring(0, Math.min(text.length(), 500)); // Limit length
    }

    private String buildContextSummary(ConversationContext context) {
        if (context.getKeywordCounts().isEmpty()) return "";

        StringBuilder summary = new StringBuilder("Bối cảnh: ");
        context.getKeywordCounts().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(1) // Only top indicator to keep it short
                .forEach(entry -> summary.append(entry.getKey()));

        return summary.toString();
    }

    /**
     * Improved trait score parsing with validation
     */
    private Map<TraitType, Integer> parseTraitScores(String response) {
        try {
            String jsonStr = extractJsonFromResponse(response);
            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            Map<TraitType, Integer> scores = new EnumMap<>(TraitType.class);

            for (TraitType trait : TraitType.values()) {
                String traitName = trait.name();
                if (jsonNode.has(traitName)) {
                    int score = jsonNode.get(traitName).asInt(1);
                    // Validate and clamp score range
                    score = Math.max(0, Math.min(3, score));
                    scores.put(trait, score);
                } else {
                    scores.put(trait, 1); // Default middle value
                }
            }
            return scores;
        } catch (Exception e) {
            log.error("Error parsing trait scores from response: {}", response, e);
            throw new RuntimeException("Could not parse trait scores", e);
        }
    }

    /**
     * Improved JSON extraction with better validation
     */
    private String extractJsonFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new RuntimeException("Empty response from API");
        }

        // Look for JSON pattern
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');

        if (start != -1 && end != -1 && end > start) {
            String jsonStr = response.substring(start, end + 1);

            // Basic validation - try to parse it
            try {
                objectMapper.readTree(jsonStr);
                return jsonStr;
            } catch (Exception e) {
                log.warn("Extracted JSON is invalid: {}", jsonStr);
            }
        }

        // Fallback: look for simpler patterns
        String[] lines = response.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("{") && line.endsWith("}")) {
                try {
                    objectMapper.readTree(line);
                    return line;
                } catch (Exception e) {
                    // Continue searching
                }
            }
        }

        throw new RuntimeException("No valid JSON found in response: " + response);
    }

    /**
     * Enhanced personality analysis with shorter, more focused prompts
     */
    public String generateValidatedPersonalityAnalysis(Map<TraitType, Integer> scores) {
        String mbtiType = calculateMBTIType(scores);
        String discType = calculateDISCType(scores);

        // Validate results
        if (!isValidMBTIType(mbtiType) || !isValidDISCType(discType)) {
            log.warn("Invalid personality types calculated: MBTI={}, DISC={}", mbtiType, discType);
            mbtiType = "ISFJ"; // Safe default
            discType = "S";    // Safe default
        }

        // More concise prompt to avoid token limits
        String prompt = String.format("""
            Tạo phân tích tính cách cho MBTI: %s, DISC: %s
            
            Format:
            NICKNAME: [Tên 2-3 từ]
            KEY TRAITS: [5 đặc điểm, cách nhau bởi dấu phẩy]
            DESCRIPTION: [2 đoạn ngắn về đặc điểm và phong cách làm việc]
            CAREER RECOMMENDATIONS: [6 nghề phù hợp]
            DEVELOPMENT TIPS: [3 gợi ý phát triển]
            """, mbtiType, discType);

        return generateResponse(prompt);
    }

    /**
     * Enhanced response generation with comprehensive retry logic
     */
    private String generateResponseWithRetry(String prompt, int retryCount) {
        try {
            String requestBody = createSafeRequestBody(prompt);

            log.debug("Sending request to Gemini (attempt {}): {} characters",
                    retryCount + 1, requestBody.length());

            String response = geminiWebClient.post()
                    .uri("/gemini-2.5-flash:generateContent")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(45)) // Increased timeout
                    .block();

            if (response == null || response.trim().isEmpty()) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            JsonNode responseNode = objectMapper.readTree(response);

            // Better response extraction
            JsonNode candidates = responseNode.path("candidates");
            if (candidates.isEmpty()) {
                throw new RuntimeException("No candidates in response");
            }

            JsonNode content = candidates.get(0).path("content");
            if (content.isMissingNode()) {
                throw new RuntimeException("No content in response");
            }

            JsonNode parts = content.path("parts");
            if (parts.isEmpty()) {
                throw new RuntimeException("No parts in response");
            }

            String text = parts.get(0).path("text").asText();
            if (text.isEmpty()) {
                throw new RuntimeException("Empty text in response");
            }

            return text;

        } catch (WebClientResponseException e) {
            log.error("WebClient error (attempt {}): {} - {}",
                    retryCount + 1, e.getStatusCode(), e.getResponseBodyAsString());

            // Retry for both 429 and 400 errors (with different strategies)
            if ((e.getStatusCode().value() == 429 || e.getStatusCode().value() == 400)
                    && retryCount < MAX_RETRIES) {

                try {
                    long delay = RETRY_DELAY_MS * (retryCount + 1);
                    if (e.getStatusCode().value() == 400) {
                        delay = 5000; // Shorter delay for 400 errors
                        // For 400 errors, try with a simpler prompt
                        if (retryCount > 1) {
                            prompt = simplifyPrompt(prompt);
                        }
                    }

                    log.info("Retrying after {} ms (attempt {})", delay, retryCount + 2);
                    Thread.sleep(delay);
                    return generateResponseWithRetry(prompt, retryCount + 1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error generating response (attempt {})", retryCount + 1, e);
            if (retryCount < MAX_RETRIES) {
                try {
                    Thread.sleep(5000);
                    return generateResponseWithRetry(simplifyPrompt(prompt), retryCount + 1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
            throw new RuntimeException("Cannot generate response after retries", e);
        }
    }

    /**
     * Simplify prompt for retry attempts
     */
    private String simplifyPrompt(String originalPrompt) {
        // Remove context and make it more basic
        if (originalPrompt.length() > 1000) {
            return "Trả lời ngắn gọn: " + originalPrompt.substring(0, 500) + "...";
        }
        return originalPrompt;
    }

    // Keep existing utility methods
    public String generateResponse(String prompt) {
        return generateResponseWithRetry(prompt, 0);
    }

    private Map<TraitType, Integer> getAdaptiveDefaultScores(String message) {
        Map<TraitType, Integer> scores = new EnumMap<>(TraitType.class);
        String lowerMessage = message.toLowerCase();

        // Simple heuristics for fallback
        scores.put(TraitType.E, lowerMessage.length() > 100 ? 2 : 1);
        scores.put(TraitType.I, lowerMessage.length() <= 100 ? 2 : 1);
        scores.put(TraitType.N, lowerMessage.contains("nghĩ") || lowerMessage.contains("ý tưởng") ? 2 : 1);
        scores.put(TraitType.S, lowerMessage.contains("thực tế") || lowerMessage.contains("kinh nghiệm") ? 2 : 1);
        scores.put(TraitType.T, lowerMessage.contains("lý do") || lowerMessage.contains("phân tích") ? 2 : 1);
        scores.put(TraitType.F, lowerMessage.contains("cảm") || lowerMessage.contains("người") ? 2 : 1);
        scores.put(TraitType.J, lowerMessage.contains("kế hoạch") || lowerMessage.contains("tổ chức") ? 2 : 1);
        scores.put(TraitType.P, lowerMessage.contains("linh hoạt") || lowerMessage.contains("thích ứng") ? 2 : 1);

        // DISC defaults
        scores.put(TraitType.D_DISC, 1);
        scores.put(TraitType.I_DISC, 1);
        scores.put(TraitType.S_DISC, 1);
        scores.put(TraitType.C_DISC, 1);

        return scores;
    }

    public void clearSessionContext(String sessionId) {
        sessionContexts.remove(sessionId);
        log.debug("Cleared context for session: {}", sessionId);
    }

    // Validation methods
    private boolean isValidMBTIType(String mbtiType) {
        return mbtiType != null && mbtiType.length() == 4 &&
                mbtiType.matches("[EI][NS][TF][JP]");
    }

    private boolean isValidDISCType(String discType) {
        return discType != null && discType.matches("[DISC]");
    }

    private String calculateMBTIType(Map<TraitType, Integer> scores) {
        StringBuilder mbti = new StringBuilder();
        mbti.append(scores.getOrDefault(TraitType.E, 0) >= scores.getOrDefault(TraitType.I, 0) ? "E" : "I");
        mbti.append(scores.getOrDefault(TraitType.N, 0) >= scores.getOrDefault(TraitType.S, 0) ? "N" : "S");
        mbti.append(scores.getOrDefault(TraitType.T, 0) >= scores.getOrDefault(TraitType.F, 0) ? "T" : "F");
        mbti.append(scores.getOrDefault(TraitType.J, 0) >= scores.getOrDefault(TraitType.P, 0) ? "J" : "P");
        return mbti.toString();
    }

    private String calculateDISCType(Map<TraitType, Integer> scores) {
        return scores.entrySet().stream()
                .filter(e -> e.getKey().name().endsWith("_DISC"))
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().name().substring(0, 1))
                .orElse("S");
    }
}
