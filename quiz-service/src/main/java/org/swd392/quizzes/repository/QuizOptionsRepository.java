package org.swd392.quizzes.repository;

import org.swd392.quizzes.entity.QuizOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizOptionsRepository extends JpaRepository<QuizOptions, Long> {

    List<QuizOptions> findByQuestionId(Long questionId);
    List<QuizOptions> findByQuestionIdIn(List<Long> questionIds);
    List<QuizOptions> findByTargetTrait(String targetTrait);
    List<QuizOptions> findByScoreValue(QuizOptions.ScoreValue scoreValue);
    List<QuizOptions> findByQuestionIdAndTargetTrait(Long questionId, String targetTrait);
    long countByQuestionId(Long questionId);
    void deleteByQuestionId(Long questionId);
}

