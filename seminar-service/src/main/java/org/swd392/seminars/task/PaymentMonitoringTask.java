package org.swd392.seminars.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.swd392.seminars.entity.SagaTransaction;
import org.swd392.seminars.event.PaymentCallbackEvent;
import org.swd392.seminars.repository.SagaTransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMonitoringTask {

    private final SagaTransactionRepository sagaTransactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 30000) // Check every 30 seconds
    public void monitorPendingPayments() {
        log.debug("Checking for pending payments...");
        
        List<SagaTransaction> pendingPayments = sagaTransactionRepository
                .findByCurrentStep(SagaTransaction.SagaStep.PAYMENT_PENDING_EXTERNAL);
        
        for (SagaTransaction saga : pendingPayments) {
            // Check if payment is still pending after timeout (e.g., 15 minutes)
            if (saga.getCreatedAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
                log.warn("Payment timeout for sagaId: {}", saga.getId());
                
                // Trigger compensation - Convert String paymentOrderCode to Long
                Long paymentOrderCode = null;
                try {
                    paymentOrderCode = Long.parseLong(saga.getPaymentOrderCode());
                } catch (NumberFormatException e) {
                    log.error("Invalid payment order code format: {}", saga.getPaymentOrderCode());
                    continue;
                }
                
                PaymentCallbackEvent event = new PaymentCallbackEvent(
                    paymentOrderCode,  // Now Long instead of String
                    false, 
                    "Payment timeout"
                );
                eventPublisher.publishEvent(event);
            }
        }
    }

    @Scheduled(fixedDelay = 60000) // Check every minute
    public void alertStuckSagas() {
        List<SagaTransaction> stuck = sagaTransactionRepository
                .findByCurrentStepAndCreatedAtBefore(
                    SagaTransaction.SagaStep.PAYMENT_PENDING_EXTERNAL,
                    LocalDateTime.now().minusHours(1)
                );
        
        if (!stuck.isEmpty()) {
            log.error("Found {} stuck sagas that need attention", stuck.size());
            // You can add alert service here if needed
        }
    }
} 