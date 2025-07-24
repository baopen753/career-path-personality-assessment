package org.swd392.quizzes.repository;

import org.swd392.quizzes.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByQuizIdAndUserId(Long quizId, Long userId);

    Integer countByQuizIdAndUserId(Long quizId, Long userId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.userId = :userId AND qr.timeSubmit BETWEEN :startDate AND :endDate")
    List<QuizResult> findByUserIdAndDateRange(@Param("userId") Long userId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
    @Query("SELECT qr FROM QuizResult qr LEFT JOIN FETCH qr.personalityStandard WHERE qr.userId = :userId ORDER BY qr.timeSubmit DESC")
    List<QuizResult> findByUserIdWithPersonalityDetails(@Param("userId") Long userId);

}
