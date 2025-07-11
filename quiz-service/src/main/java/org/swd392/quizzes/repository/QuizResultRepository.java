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

    List<QuizResult> findByUserIdOrderByTimeSubmitDesc(String userId);

    List<QuizResult> findByQuizIdAndUserId(Long quizId, String userId);

    Integer countByQuizIdAndUserId(Long quizId, String userId);

    List<QuizResult> findByQuizId(Long quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.userId = :userId AND qr.timeSubmit BETWEEN :startDate AND :endDate")
    List<QuizResult> findByUserIdAndDateRange(@Param("userId") String userId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT qr.resultType, COUNT(qr) FROM QuizResult qr GROUP BY qr.resultType ORDER BY COUNT(qr) DESC")
    List<Object[]> findMostCommonPersonalityTypes();

    @Query("SELECT qr FROM QuizResult qr LEFT JOIN FETCH qr.personalityStandard WHERE qr.userId = :userId")
    List<QuizResult> findByUserIdWithPersonalityStandard(@Param("userId") String userId);

    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.quizId = :quizId")
    Long countByQuizId(@Param("quizId") Long quizId);

    boolean existsByQuizIdAndUserId(Long quizId, String userId);

    @Query("SELECT qr FROM QuizResult qr LEFT JOIN FETCH qr.personalityStandard WHERE qr.userId = :userId ORDER BY qr.timeSubmit DESC")
    List<QuizResult> findByUserIdWithPersonalityDetails(@Param("userId") String userId);

    @Query(value = """
            SELECT DISTINCT qr.user_id 
            FROM quiz_result qr 
            WHERE qr.result_json::jsonb ->> 'email' = :email 
               OR qr.result_json::jsonb -> 'user' ->> 'email' = :email
               OR qr.result_json::jsonb ->> 'userEmail' = :email
               OR qr.result_json::jsonb ->> 'submittedBy' = :email
               OR EXISTS (
                   SELECT 1 
                   FROM quiz_result qr2 
                   WHERE qr2.user_id = qr.user_id 
                   AND qr2.result_json::jsonb ->> 'email' = :email
               )
            """, nativeQuery = true)
    String findUserIdByEmailInResultJson(@Param("email") String email);

    @Query(value = """
            SELECT qr.* FROM quiz_result qr 
            WHERE qr.user_id IN (
                SELECT DISTINCT user_id 
                FROM quiz_result 
                WHERE result_json::jsonb ->> 'email' = :email 
                   OR result_json::jsonb -> 'user' ->> 'email' = :email
                   OR result_json::jsonb ->> 'userEmail' = :email
                   OR result_json::jsonb ->> 'submittedBy' = :email
            )
            ORDER BY qr.time_submit DESC
            """, nativeQuery = true)
    List<QuizResult> findByEmailInResultJson(@Param("email") String email);
}
