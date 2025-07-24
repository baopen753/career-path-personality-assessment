package org.swd392.seminars.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.seminars.dto.UserInfoDto;
import org.swd392.seminars.entity.SagaTransaction;
import org.swd392.seminars.event.PaymentCallbackEvent;
import org.swd392.seminars.event.TicketBookedEvent;
import org.swd392.seminars.event.producer.EventProducer;
import org.swd392.seminars.repository.SagaTransactionRepository;
import org.swd392.seminars.service.SeminarTicketService;
import org.swd392.seminars.service.client.UserFeignClient;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCallbackEventListener {
    
    private final SagaTransactionRepository sagaRepository;
    private final UserFeignClient userFeignClient;
    private final EventProducer<TicketBookedEvent> eventProducer;
    private final SeminarTicketService seminarTicketService;
    
    @EventListener
    @Transactional
    public void handlePaymentCallback(PaymentCallbackEvent event) {
        log.info("Processing payment callback event: orderCode={}, success={}, message={}", 
                event.getPaymentOrderCode(), event.isSuccess(), event.getMessage());
        
        try {
            // Tìm saga transaction theo payment order code (convert Long to String)
            String orderCodeStr = event.getPaymentOrderCodeAsString();
            Optional<SagaTransaction> sagaOpt = sagaRepository.findByPaymentOrderCode(orderCodeStr);
            
            if (sagaOpt.isEmpty()) {
                log.error("Saga transaction not found for payment order code: {}", orderCodeStr);
                return;
            }
            
            SagaTransaction saga = sagaOpt.get();
            log.info("Found saga transaction: id={}, userId={}, seminarId={}, currentStatus={}, currentStep={}", 
                    saga.getId(), saga.getUserId(), saga.getSeminarId(), saga.getStatus(), saga.getCurrentStep());
            
            if (event.isSuccess()) {
                // Payment successful - complete saga
                saga.setStatus(SagaTransaction.SagaStatus.COMPLETED);
                saga.setCurrentStep(SagaTransaction.SagaStep.PAYMENT_COMPLETED);
                saga.setUpdatedAt(LocalDateTime.now());
                
                sagaRepository.save(saga);
                
                // Get user details for notification
                try {
                    var userInfo = userFeignClient.getUserDetails(Long.valueOf(saga.getUserId()));
                    
                    if (userInfo.getBody() != null) {
                        TicketBookedEvent ticketEvent = convertToTicketBookedEvent(saga, userInfo.getBody().getResult());
                        eventProducer.sendMessage(ticketEvent);
                        log.info("✅ Sent ticket booked event for saga {}", saga.getId());
                    }
                } catch (Exception e) {
                    log.warn("Failed to send ticket booked notification for saga {}: {}", saga.getId(), e.getMessage());
                }
                
                log.info("✅ Successfully completed saga {} after successful payment: status={}, step={}", 
                        saga.getId(), saga.getStatus(), saga.getCurrentStep());
            } else {
                // Payment failed - mark saga as failed and compensate
                saga.setStatus(SagaTransaction.SagaStatus.FAILED);
                saga.setCurrentStep(SagaTransaction.SagaStep.COMPENSATION_REQUIRED);
                saga.setUpdatedAt(LocalDateTime.now());
                
                sagaRepository.save(saga);
                
                // Trigger compensation
                try {
                    compensateBookingSaga(saga);
                } catch (Exception e) {
                    log.error("Failed to compensate saga {}: {}", saga.getId(), e.getMessage());
                }
                
                log.warn("❌ Marked saga {} as failed due to payment failure: status={}, step={}, message={}", 
                        saga.getId(), saga.getStatus(), saga.getCurrentStep(), event.getMessage());
            }
            
        } catch (Exception e) {
            log.error("Error processing payment callback for order code {}: {}", 
                    event.getPaymentOrderCodeAsString(), e.getMessage(), e);
        }
    }
    
    private TicketBookedEvent convertToTicketBookedEvent(SagaTransaction sagaTransaction, UserInfoDto userInfo) {
        return TicketBookedEvent.builder()
                .userId(sagaTransaction.getUserId())
                .paymentOrderCode(sagaTransaction.getPaymentOrderCode())
                .email(userInfo.getEmail())
                .fullName(userInfo.getFullName())
                .status(sagaTransaction.getStatus().name())
                .createdAt(sagaTransaction.getCreatedAt())
                .amount(sagaTransaction.getAmount())
                .paymentMethod(sagaTransaction.getPaymentMethod())
                .build();
    }
    
    private void compensateBookingSaga(SagaTransaction sagaTransaction) {
        try {
            log.info("Starting compensation for saga id: {}", sagaTransaction.getId());
            
            // Cancel the booking
            seminarTicketService.deleteBookedTicket(sagaTransaction.getSeminarId(), sagaTransaction.getUserId());
            
            log.info("Compensation completed for saga id: {}", sagaTransaction.getId());
            
        } catch (Exception e) {
            log.error("Compensation failed for saga id: {}", sagaTransaction.getId(), e);
        }
    }
}
