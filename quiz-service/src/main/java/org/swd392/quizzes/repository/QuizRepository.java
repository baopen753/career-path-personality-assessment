package org.swd392.quizzes.repository;
import org.swd392.quizzes.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByCategoryId(Long categoryId);

    List<Quiz> findByTitleContaining(String title);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Quiz findByIdWithQuestions(@Param("id") Long id);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.category WHERE q.id = :id")
    Quiz findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.categoryId = :categoryId")
    Long countByCategoryId(@Param("categoryId") Long categoryId);
}
