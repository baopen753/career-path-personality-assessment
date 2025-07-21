package org.swd392.chatbox.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analysis_results", indexes = {
        @Index(name = "idx_analysisresult_user_id", columnList = "user_id"),
        @Index(name = "idx_analysisresult_session_id", columnList = "session_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "mbti_type", length = 10)
    private String mbtiType;

    @Column(name = "disc_type", length = 50)
    private String discType;

    @Column(name = "extraversion")
    private Integer extraversion;

    @Column(name = "intuition")
    private Integer intuition;

    @Column(name = "thinking")
    private Integer thinking;

    @Column(name = "judging")
    private Integer judging;

    @Column(name = "dominance")
    private Integer dominance;

    @Column(name = "influence")
    private Integer influence;

    @Column(name = "steadiness")
    private Integer steadiness;

    @Column(name = "compliance")
    private Integer compliance;

    @Column(name = "key_traits", columnDefinition = "TEXT")
    private String keyTraits;

    @Column(name = "suitable_careers", columnDefinition = "TEXT")
    private String suitableCareers;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String analysis;

    @Column(name = "development_suggestions", columnDefinition = "TEXT")
    private String developmentSuggestions;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
