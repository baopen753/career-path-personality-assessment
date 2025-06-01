package org.swd392.quizzes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "result_type")
    private String resultType;

    @Column(name = "time_submit")
    private LocalDateTime timeSubmit;

    @Column(name = "attempt_order")
    private Integer attemptOrder;

    @Column(name = "result_json", columnDefinition = "jsonb")
    private String resultJson;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "personality_id")
    private Long personalityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personality_id", insertable = false, updatable = false)
    private PersonalityStandard personalityStandard;
}
