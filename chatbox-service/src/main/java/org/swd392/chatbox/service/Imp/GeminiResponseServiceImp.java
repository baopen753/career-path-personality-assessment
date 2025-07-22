package org.swd392.chatbox.service.Imp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.swd392.chatbox.service.GeminiResponseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Service
@Slf4j
public class GeminiResponseServiceImp implements GeminiResponseService {

    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 20000;
    private static final int MAX_PROMPT_LENGTH = 15000;
    private static final int MAX_OUTPUT_TOKENS = 4000;

    public GeminiResponseServiceImp(WebClient geminiWebClient) {
        this.geminiWebClient = geminiWebClient;
        this.objectMapper = new ObjectMapper();
    }

    private String createSafeRequestBody(String prompt) {
        try {
            String truncatedPrompt = truncatePrompt(prompt, MAX_PROMPT_LENGTH);

            ObjectNode requestNode = objectMapper.createObjectNode();
            ObjectNode contentNode = objectMapper.createObjectNode();
            ObjectNode partNode = objectMapper.createObjectNode();
            ObjectNode configNode = objectMapper.createObjectNode();

            partNode.put("text", truncatedPrompt);

            contentNode.set("parts", objectMapper.createArrayNode().add(partNode));
            requestNode.set("contents", objectMapper.createArrayNode().add(contentNode));

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

    private String truncatePrompt(String prompt, int maxLength) {
        if (prompt.length() <= maxLength) {
            return prompt;
        }

        log.warn("Prompt too long ({}), truncating to {} characters", prompt.length(), maxLength);

        String truncated = prompt.substring(0, maxLength);
        int lastNewline = truncated.lastIndexOf('\n');
        int lastPeriod = truncated.lastIndexOf('.');

        if (lastNewline > maxLength * 0.8) {
            return prompt.substring(0, lastNewline) + "\n[Nội dung đã được rút gọn]";
        } else if (lastPeriod > maxLength * 0.8) {
            return prompt.substring(0, lastPeriod + 1) + " [Nội dung đã được rút gọn]";
        }

        return truncated + "... [Nội dung đã được rút gọn]";
    }

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
                    .timeout(Duration.ofSeconds(45))
                    .block();

            if (response == null || response.trim().isEmpty()) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            JsonNode responseNode = objectMapper.readTree(response);

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

            // trả lỗi 429 and 400 errors
            if ((e.getStatusCode().value() == 429 || e.getStatusCode().value() == 400)
                    && retryCount < MAX_RETRIES) {

                try {
                    long delay = RETRY_DELAY_MS * (retryCount + 1);
                    if (e.getStatusCode().value() == 400) {
                        delay = 5000; // trả delay để kiểm soát lỗi 400 errors
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

    private String simplifyPrompt(String originalPrompt) {
        if (originalPrompt.length() > 1000) {
            return "Trả lời ngắn gọn: " + originalPrompt.substring(0, 500) + "...";
        }
        return originalPrompt;
    }

    public String generateResponse(String prompt) {
        return generateResponseWithRetry(prompt, 0);
    }
}
