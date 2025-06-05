package org.swd392.quizzes.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swd392.quizzes.entity.QuizResult;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByUserIdOrderByTimeSubmitDesc(Long userId);

    List<QuizResult> findByQuizIdAndUserId(Long quizId, Long userId);

    Integer countByQuizIdAndUserId(Long quizId, Long userId);

    List<QuizResult> findByQuizId(Long quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.userId = :userId AND qr.timeSubmit BETWEEN :startDate AND :endDate")
    List<QuizResult> findByUserIdAndDateRange(@Param("userId") Long userId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT qr.resultType, COUNT(qr) FROM QuizResult qr GROUP BY qr.resultType ORDER BY COUNT(qr) DESC")
    List<Object[]> findMostCommonPersonalityTypes();

    @Query("SELECT qr FROM QuizResult qr LEFT JOIN FETCH qr.personalityStandard WHERE qr.userId = :userId")
    List<QuizResult> findByUserIdWithPersonality(@Param("userId") Long userId);

    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.quizId = :quizId")
    Long countByQuizId(@Param("quizId") Long quizId);

    boolean existsByQuizIdAndUserId(Long quizId, Long userId);
}

