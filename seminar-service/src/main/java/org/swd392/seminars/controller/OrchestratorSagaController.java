package org.swd392.seminars.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.seminars.entity.SagaTransaction;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.payload.response.PaymentInitiationResponse;
import org.swd392.seminars.repository.SagaTransactionRepository;
import org.swd392.seminars.service.OrchestratorSagaService;

@Slf4j
@RestController
@RequestMapping("/api/place-order")
@RequiredArgsConstructor
@Tag(name = "Seminar Ticket Management", description = "APIs for managing seminar tickets")
public class OrchestratorSagaController {

    private final OrchestratorSagaService orchestratorSagaService;
    private final SagaTransactionRepository sagaTransactionRepository;

    @PostMapping
    public ResponseEntity<PaymentInitiationResponse> placeOrderSaga(@RequestHeader("X-User-Id") Integer userId,
                                                                    @Valid @RequestBody SeminarTicketRequest request) {
        PaymentInitiationResponse response = orchestratorSagaService.startBookTicketSaga(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{sagaId}")
    public ResponseEntity<SagaTransaction> getSagaStatus(@PathVariable Long sagaId) {
        SagaTransaction sagaTransaction = sagaTransactionRepository.findById(sagaId)
                .orElseThrow(() -> new RuntimeException("Saga transaction not found"));
        return ResponseEntity.ok(sagaTransaction);
    }
}