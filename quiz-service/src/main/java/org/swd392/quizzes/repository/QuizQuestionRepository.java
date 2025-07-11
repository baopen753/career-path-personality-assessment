package org.swd392.quizzes.repository;

import org.swd392.quizzes.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findByQuizIdOrderByOrderNumber(Long quizId);

    List<QuizQuestion> findByDimension(String dimension);

    @Query("SELECT DISTINCT qq FROM QuizQuestion qq LEFT JOIN FETCH qq.options o WHERE qq.quizId = :quizId ORDER BY qq.orderNumber, o.id")
    List<QuizQuestion> findByQuizIdWithOptions(@Param("quizId") Long quizId);

    // Batch fetch multiple quizzes questions at once
    @Query("SELECT DISTINCT qq FROM QuizQuestion qq LEFT JOIN FETCH qq.options o WHERE qq.quizId IN :quizIds ORDER BY qq.quizId, qq.orderNumber, o.id")
    List<QuizQuestion> findByQuizIdsWithOptions(@Param("quizIds") List<Long> quizIds);

    @Query("SELECT COUNT(qq) FROM QuizQuestion qq WHERE qq.quizId = :quizId")
    Long countByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COALESCE(MAX(qq.orderNumber), 0) FROM QuizQuestion qq WHERE qq.quizId = :quizId")
    Integer findMaxOrderNumberByQuizId(@Param("quizId") Long quizId);

    // Optimized query for getting questions by dimension with options pre-loaded
    @Query("SELECT DISTINCT qq FROM QuizQuestion qq LEFT JOIN FETCH qq.options WHERE qq.dimension = :dimension ORDER BY qq.orderNumber")
    List<QuizQuestion> findByDimensionWithOptions(@Param("dimension") String dimension);
}
