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

    @Query("SELECT qq FROM QuizQuestion qq LEFT JOIN FETCH qq.options WHERE qq.quizId = :quizId ORDER BY qq.orderNumber")
    List<QuizQuestion> findByQuizIdWithOptions(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(qq) FROM QuizQuestion qq WHERE qq.quizId = :quizId")
    Long countByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT MAX(qq.orderNumber) FROM QuizQuestion qq WHERE qq.quizId = :quizId")
    Integer findMaxOrderNumberByQuizId(@Param("quizId") Long quizId);
}


