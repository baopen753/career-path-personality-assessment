package org.swd392.seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swd392.seminars.entity.SagaTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SagaTransactionRepository extends JpaRepository<SagaTransaction, Long> {
    
    Optional<SagaTransaction> findByPaymentOrderCode(String paymentOrderCode);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM SagaTransaction s " +
            "WHERE s.userId = :userId AND s.seminarId = :seminarId AND s.status = 'COMPLETED'")
    boolean existsByUserIdAndSeminarIdAndStatus_Completed(@Param("userId") Integer userId, @Param("seminarId") Integer seminarId);
    
    List<SagaTransaction> findByCurrentStep(SagaTransaction.SagaStep currentStep);
    
    @Query("SELECT s FROM SagaTransaction s WHERE s.currentStep = :step AND s.createdAt < :before")
    List<SagaTransaction> findByCurrentStepAndCreatedAtBefore(
            @Param("step") SagaTransaction.SagaStep step,
            @Param("before") LocalDateTime before
    );
    
    List<SagaTransaction> findByStatus(SagaTransaction.SagaStatus status);
} 