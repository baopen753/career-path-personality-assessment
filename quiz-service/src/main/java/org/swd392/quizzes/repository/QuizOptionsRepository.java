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

    List<QuizOptions> findByTargetTrait(String targetTrait);

    @Query("SELECT qo FROM QuizOptions qo WHERE qo.questionId IN :questionIds")
    List<QuizOptions> findByQuestionIds(@Param("questionIds") List<Long> questionIds);

    @Query("SELECT COUNT(qo) FROM QuizOptions qo WHERE qo.questionId = :questionId")
    Long countByQuestionId(@Param("questionId") Long questionId);
}

