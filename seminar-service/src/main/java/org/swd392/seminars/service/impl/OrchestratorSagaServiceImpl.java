package org.swd392.seminars.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.seminars.dto.PaymentRequestDTO;
import org.swd392.seminars.entity.SagaTransaction;
import org.swd392.seminars.event.PaymentCallbackEvent;
import org.swd392.seminars.exception.SagaTransactionException;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.payload.response.PaymentInitiationResponse;
import org.swd392.seminars.payload.response.SeminarTicketResponse;
import org.swd392.seminars.repository.SagaTransactionRepository;
import org.swd392.seminars.service.OrchestratorSagaService;
import org.swd392.seminars.service.SeminarTicketService;
import org.swd392.seminars.service.client.PaymentFeignClient;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestratorSagaServiceImpl implements OrchestratorSagaService {

    private final PaymentFeignClient paymentFeignClient;
    private final SeminarTicketService seminarTicketService;
    private final SagaTransactionRepository sagaTransactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PaymentInitiationResponse startBookTicketSaga(Integer userId, SeminarTicketRequest request) {

        // Check if saga already exists for this user and seminar
        if (sagaTransactionRepository.existsByUserIdAndSeminarId(userId, request.getSeminarId())) {
            log.warn("Saga already exists for user {} and seminar {}", userId, request.getSeminarId());
            throw new SagaTransactionException("Booking already in progress");
        }

        // Create a saga transaction
        SagaTransaction sagaTransaction = SagaTransaction.builder()
                .userId(userId)
                .seminarId(request.getSeminarId())
                .status(SagaTransaction.SagaStatus.PENDING)
                .currentStep(SagaTransaction.SagaStep.BOOKING_INITIATED)
                .build();

        sagaTransactionRepository.save(sagaTransaction);
        log.info("Created saga transaction with id: {}", sagaTransaction.getId());

        try {
            // Step 1: Book ticket
            request.setUserId(userId);
            SeminarTicketResponse seminarTicketResponse = seminarTicketService.bookTicket(request);

            // Update saga state
            sagaTransaction.setCurrentStep(SagaTransaction.SagaStep.BOOKING_COMPLETED);
            sagaTransaction.setStatus(SagaTransaction.SagaStatus.BOOKING_COMPLETED);
            sagaTransactionRepository.save(sagaTransaction);
            log.info("Booking completed for saga id: {}", sagaTransaction.getId());

            // Step 2: Initiate payment with PayOS
            PaymentRequestDTO paymentRequest = convertToPaymentRequestDto(request, sagaTransaction.getId());

            // Call payment service
            var paymentResponse = paymentFeignClient.createPayment(paymentRequest);

            // Extract payment details
            String paymentOrderCode = String.valueOf(paymentResponse.getBody() != null ? paymentResponse.getBody().getOrderCode() : null);
            String checkoutUrl = paymentResponse.getBody() != null ? paymentResponse.getBody().getCheckoutUrl() : null;

            // Update saga with payment reference
            sagaTransaction.setPaymentOrderCode(paymentOrderCode);
            sagaTransaction.setCurrentStep(SagaTransaction.SagaStep.PAYMENT_PENDING_EXTERNAL);
            sagaTransaction.setStatus(SagaTransaction.SagaStatus.PAYMENT_PENDING);
            sagaTransactionRepository.save(sagaTransaction);

            log.info("Payment initiated for saga id: {}, payment order code: {}, checkout URL: {}",
                    sagaTransaction.getId(), paymentOrderCode, checkoutUrl);

            // Return payment initiation response to client
            return PaymentInitiationResponse.builder()
                    .sagaId(sagaTransaction.getId())
                    .orderCode(paymentOrderCode)
                    .checkoutUrl(checkoutUrl)
                    .status(SagaTransaction.SagaStatus.PAYMENT_PENDING.getDescription())
                    .message("Payment initiated successfully. Please complete payment using the provided URL.")
                    .build();

        } catch (Exception e) {
            log.error("Saga failed at step: {} for saga id: {}",
                    sagaTransaction.getCurrentStep(), sagaTransaction.getId(), e);
            compensateBookingSaga(sagaTransaction);
            throw new SagaTransactionException("Failed to initiate payment: " + e.getMessage());
        }
    }

    @Override
    public SagaTransaction findSagaTransactionByPaymentOrderCode(String paymentOrderCode) {
        return sagaTransactionRepository.findByPaymentOrderCode(paymentOrderCode)
                .orElseThrow(() -> new SagaTransactionException("Saga transaction not found for payment order code: " + paymentOrderCode));
    }

    // Handle PayOS callback
    @EventListener
    @Transactional
    public void handlePaymentCallback(PaymentCallbackEvent event) {
        log.info("Received payment callback for payment order code: {}, success: {}",
                event.getPaymentOrderCode(), event.isSuccess());

        SagaTransaction sagaTransaction = sagaTransactionRepository
                .findByPaymentOrderCode(event.getPaymentOrderCode())
                .orElseThrow(() -> new SagaTransactionException("Saga transaction not found for payment order code: " + event.getPaymentOrderCode()));

        if (event.isSuccess()) {
            // Payment successful
            sagaTransaction.setCurrentStep(SagaTransaction.SagaStep.PAYMENT_COMPLETED);
            sagaTransaction.setStatus(SagaTransaction.SagaStatus.COMPLETED);
            sagaTransactionRepository.save(sagaTransaction);

            log.info("Saga completed successfully for saga id: {}", sagaTransaction.getId());

        } else {
            // Payment failed - trigger compensation
            log.error("Payment failed for saga id: {}, reason: {}",
                    sagaTransaction.getId(), event.getMessage());
            compensateBookingSaga(sagaTransaction);
        }
    }

    private PaymentRequestDTO convertToPaymentRequestDto(SeminarTicketRequest seminarTicketRequest, Long sagaId) {
        PaymentRequestDTO paymentRequestDTO = PaymentRequestDTO.builder()
                .amount(seminarTicketRequest.getPrice())
                .description(seminarTicketRequest.getDescription())
                .sagaId(sagaId)
                .build();
        return paymentRequestDTO;
    }

    private void compensateBookingSaga(SagaTransaction sagaTransaction) {
        try {
            log.info("Starting compensation for saga id: {}", sagaTransaction.getId());

            // Cancel the booking
            seminarTicketService.deleteBookedTicket(sagaTransaction.getSeminarId(), sagaTransaction.getUserId());

            // Update saga state
            sagaTransaction.setCurrentStep(SagaTransaction.SagaStep.COMPENSATION_REQUIRED);
            sagaTransaction.setStatus(SagaTransaction.SagaStatus.FAILED);
            sagaTransactionRepository.save(sagaTransaction);

            log.info("Compensation completed for saga id: {}", sagaTransaction.getId());

        } catch (Exception e) {
            log.error("Compensation failed for saga id: {}", sagaTransaction.getId(), e);
            // You might want to implement a retry mechanism or alert system here
        }
    }
}