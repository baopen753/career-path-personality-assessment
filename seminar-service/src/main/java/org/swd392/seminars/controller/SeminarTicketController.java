package org.swd392.seminars.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.payload.response.SeminarTicketResponse;
import org.swd392.seminars.service.SeminarTicketService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seminar-tickets")
@RequiredArgsConstructor
@Tag(name = "Seminar Ticket Management", description = "APIs for managing seminar tickets and bookings")
public class SeminarTicketController {

    private final SeminarTicketService seminarTicketService;

    @Operation(summary = "Book a seminar ticket", 
              description = "Book a ticket for a specific seminar. Users can only book one ticket per seminar.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ticket successfully booked"),
        @ApiResponse(responseCode = "400", description = "Invalid request or user already has a ticket"),
        @ApiResponse(responseCode = "404", description = "Seminar not found"),
        @ApiResponse(responseCode = "409", description = "Seminar is fully booked")
    })
    @PostMapping("/book")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<SeminarTicketResponse> bookTicket(
            @RequestHeader("X-User-Id") Integer userId,
            @RequestBody SeminarTicketRequest request) {
        request.setUserProfileId(userId);
        return new ResponseEntity<>(seminarTicketService.bookTicket(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Cancel a seminar ticket", 
              description = "Cancel a booked ticket. Only the ticket owner can cancel their ticket.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ticket successfully cancelled"),
        @ApiResponse(responseCode = "404", description = "Ticket not found"),
        @ApiResponse(responseCode = "403", description = "User not authorized to cancel this ticket")
    })
    @DeleteMapping("/{ticketId}/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> cancelTicket(
            @RequestHeader("X-User-Id") Integer userId,
            @PathVariable Integer ticketId) {
        seminarTicketService.cancelTicket(userId, ticketId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get tickets by seminar - Event Manager", 
              description = "Get all tickets booked for a specific seminar. Only accessible by event managers.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tickets"),
        @ApiResponse(responseCode = "403", description = "User not authorized to view tickets"),
        @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @GetMapping("/seminar/{seminarId}/tickets")
    @PreAuthorize("hasRole('ROLE_EVENT_MANAGER')")
    public ResponseEntity<List<SeminarTicketResponse>> getTicketsBySeminar(
            @PathVariable Integer seminarId) {
        return ResponseEntity.ok(seminarTicketService.getTicketsBySeminar(seminarId));
    }

    @Operation(summary = "Get user's tickets", 
              description = "Get all tickets booked by the current user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user's tickets"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping("/my-tickets")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<SeminarTicketResponse>> getMyTickets(
            @RequestHeader("X-User-Id") Integer userId) {
        return ResponseEntity.ok(seminarTicketService.getTicketsByUser(userId));
    }

    @Operation(summary = "Get ticket details", 
              description = "Get detailed information about a specific ticket.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket details"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @GetMapping("/{ticketId}/details")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<SeminarTicketResponse> getTicket(
            @PathVariable Integer ticketId) {
        return ResponseEntity.ok(seminarTicketService.getTicket(ticketId));
    }

    @Operation(summary = "Check ticket availability", 
              description = "Check if tickets are still available for a specific seminar.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved availability status"),
        @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @GetMapping("/{seminarId}/availability")
    public ResponseEntity<Boolean> checkTicketAvailability(
            @PathVariable Integer seminarId) {
        long bookedCount = seminarTicketService.getBookedTicketsCount(seminarId);
        return ResponseEntity.ok(bookedCount < Integer.MAX_VALUE); // Replace with actual seminar slot check
    }
}
