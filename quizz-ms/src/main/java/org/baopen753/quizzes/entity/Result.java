package org.baopen753.quizzes.entity;

import jakarta.persistence.*;
import org.baopen753.quizzes.enums.MBTIType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "results")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Integer resultId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mbit_type", nullable = false)
    private MBTIType mbtiType;

    @Column(name = "result_json", columnDefinition = "TEXT", nullable = false)
    private String resultJson;
}