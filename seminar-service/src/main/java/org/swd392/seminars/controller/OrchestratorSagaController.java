package org.swd392.seminars.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.service.OrchestratorSagaService;

@Slf4j
@RestController
@RequestMapping("/api/place-order")
@RequiredArgsConstructor
@Tag(name = "Seminar Ticket Management", description = "APIs for managing seminar tickets")
public class OrchestratorSagaController {

    private final OrchestratorSagaService orchestratorSagaService;

    @PostMapping
    public ResponseEntity<?> placeOrderSaga(@RequestHeader("X-User-Id") Integer userProfileId,
                                            @Valid @RequestBody SeminarTicketRequest request) {
        orchestratorSagaService.startBookTicketSaga(userProfileId, request);
        return ResponseEntity.ok().build();
    }
}