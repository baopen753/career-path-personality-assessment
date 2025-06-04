package org.swd392.quizzes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizOptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text", columnDefinition = "TEXT", nullable = false)
    private String optionText;

    @Column(name = "target_trait")
    private String targetTrait;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "score_value")
    private ScoreValue scoreValue;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private QuizQuestion question;

    public enum ScoreValue {
        NEGATIVE(-1), NEUTRAL(0), POSITIVE(1);

        private final int value;

        ScoreValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
