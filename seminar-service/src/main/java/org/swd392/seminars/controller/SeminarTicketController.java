package org.swd392.seminars.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.seminars.annotation.RequireRole;
import org.swd392.seminars.enums.UserRole;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.payload.response.SeminarTicketResponse;
import org.swd392.seminars.service.SeminarTicketService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Seminar Ticket Management", description = "APIs for managing seminar tickets")
public class SeminarTicketController {

    private final SeminarTicketService seminarTicketService;

    @Operation(summary = "Book a seminar ticket",
            description = "Book a ticket for a seminar. Accessible by students and parents.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket booked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ticket data provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not a student or parent"),
            @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @PostMapping("/book-ticket")
    @RequireRole({UserRole.STUDENT, UserRole.PARENT})
    public ResponseEntity<SeminarTicketResponse> bookTicket(
            @RequestHeader("X-User-Id") Integer userId,
            @Valid @RequestBody SeminarTicketRequest request) {
        log.info("User ID: {} booking ticket for seminar ID: {}", userId, request.getSeminarId());
        try {
            request.setUserId(userId);
            SeminarTicketResponse response = seminarTicketService.bookTicket(request);
            log.info("Successfully booked ticket ID: {} for seminar ID: {}", response.getId(), request.getSeminarId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error booking ticket: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/compensate")
    public ResponseEntity<?> compensate(@Valid @RequestBody SeminarTicketRequest request) {
        seminarTicketService.deleteBookedTicket(request.getSeminarId(), request.getUserId());
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Cancel a seminar ticket",
              description = "Cancel a booked ticket. Only the ticket owner can cancel their ticket.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket cancelled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not the ticket owner"),
            @ApiResponse(responseCode = "404", description = "Ticket not found")
    })

    @DeleteMapping("/{ticketId}")
    @RequireRole({UserRole.STUDENT, UserRole.PARENT})
    public ResponseEntity<Void> cancelTicket(
            @RequestHeader("X-User-Id") Integer userId,
            @PathVariable Integer ticketId) {
        log.info("User ID: {} cancelling ticket ID: {}", userId, ticketId);
        try {
            seminarTicketService.cancelTicket(userId, ticketId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error cancelling ticket: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get user's tickets",
              description = "Get all tickets booked by the current user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user's tickets"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not logged in")
    })
    @GetMapping("/my-tickets")
    @RequireRole({UserRole.STUDENT, UserRole.PARENT})
    public ResponseEntity<List<SeminarTicketResponse>> getMyTickets(
            @RequestHeader("X-User-Id") Integer userId) {
        log.info("Getting tickets for user ID: {}", userId);
        try {
            List<SeminarTicketResponse> tickets = seminarTicketService.getTicketsByUser(userId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Error getting user tickets: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get seminar tickets - Event Manager",
            description = "Get all tickets for a specific seminar. Only accessible by the event manager who created the seminar.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved seminar tickets"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Not an event manager"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not the seminar owner"),
            @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @GetMapping("/seminar/{seminarId}/tickets")
    @RequireRole(UserRole.EVENT_MANAGER)
    public ResponseEntity<List<SeminarTicketResponse>> getSeminarTickets(
            @RequestHeader("X-User-Id") Integer userId,
            @PathVariable Integer seminarId) {
        log.info("Event manager ID: {} getting tickets for seminar ID: {}", userId, seminarId);
        try {
            List<SeminarTicketResponse> tickets = seminarTicketService.getTicketsBySeminar(seminarId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Error getting seminar tickets: {}", e.getMessage());
            throw e;
        }
    }
}


