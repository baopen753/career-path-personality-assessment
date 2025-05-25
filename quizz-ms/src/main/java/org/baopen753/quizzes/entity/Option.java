package org.baopen753.quizzes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.baopen753.quizzes.enums.ScoreValue;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "options")
public class Option {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer optionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "option_text", columnDefinition = "TEXT", nullable = false)
    private ScoreValue scopedValue;

   
    @Column(name = "score_value", nullable = false)
    private Integer scoreValue;
}
