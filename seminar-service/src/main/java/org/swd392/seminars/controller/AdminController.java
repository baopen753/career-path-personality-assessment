package org.swd392.seminars.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.swd392.seminars.entity.SagaTransaction;
import org.swd392.seminars.repository.SagaTransactionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final SagaTransactionRepository sagaRepository;
    
    @PostMapping("/fix-saga/{paymentOrderCode}")
    public String fixSagaStatus(@PathVariable String paymentOrderCode) {
        log.info("Manually fixing saga status for payment order code: {}", paymentOrderCode);
        
        Optional<SagaTransaction> sagaOpt = sagaRepository.findByPaymentOrderCode(paymentOrderCode);
        
        if (sagaOpt.isEmpty()) {
            return "Saga not found for payment order code: " + paymentOrderCode;
        }
        
        SagaTransaction saga = sagaOpt.get();
        
        if (saga.getStatus() == SagaTransaction.SagaStatus.COMPLETED) {
            return "Saga already completed for payment order code: " + paymentOrderCode;
        }
        
        // Update saga to completed
        saga.setStatus(SagaTransaction.SagaStatus.COMPLETED);
        saga.setCurrentStep(SagaTransaction.SagaStep.PAYMENT_COMPLETED);
        saga.setUpdatedAt(LocalDateTime.now());
        
        sagaRepository.save(saga);
        
        log.info("✅ Manually fixed saga {} to COMPLETED status", saga.getId());
        
        return "Successfully fixed saga " + saga.getId() + " to COMPLETED status";
    }
}
