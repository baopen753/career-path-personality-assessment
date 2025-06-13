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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.swd392.seminars.entity.Seminar;
import org.swd392.seminars.exception.ResourceNotFoundException;
import org.swd392.seminars.exception.SeminarTicketException;
import org.swd392.seminars.exception.UnauthorizedException;
import org.swd392.seminars.payload.request.SeminarRequest;
import org.swd392.seminars.payload.response.SeminarResponse;
import org.swd392.seminars.service.SeminarService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/seminars")
@RequiredArgsConstructor
@Tag(name = "Seminar Management", description = "APIs for managing seminars")
public class SeminarController {

    private final SeminarService seminarService;

    @Operation(summary = "Create a new seminar request - Event Manager", 
              description = "Create a new seminar request that needs admin approval. Only accessible by event managers.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Seminar request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid seminar data provided"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an event manager"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @PostMapping("/create-seminar")
    @PreAuthorize("hasRole('EVENT_MANAGER')")
    public ResponseEntity<SeminarResponse> createSeminar(
            @RequestHeader("X-User-Id") Integer eventManagerId,
            @Valid @RequestBody SeminarRequest request) {
        log.info("Creating new seminar by event manager ID: {}", eventManagerId);
        try {
            SeminarResponse response = seminarService.createSeminar(eventManagerId, request);
            log.info("Successfully created seminar with ID: {}", response.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating seminar: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Update a pending seminar - Event Manager", 
              description = "Update a seminar that hasn't been approved yet. Only accessible by the event manager who created it.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Seminar updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid seminar data provided"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an event manager"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the seminar owner or seminar already approved"),
        @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @PutMapping("/{seminarId}/update-seminar")
    @PreAuthorize("hasRole('EVENT_MANAGER')")
    public ResponseEntity<SeminarResponse> updateSeminar(
            @RequestHeader("X-User-Id") Integer eventManagerId,
            @PathVariable Integer seminarId,
            @Valid @RequestBody SeminarRequest request) {
        log.info("Updating seminar ID: {} by event manager ID: {}", seminarId, eventManagerId);
        try {
            SeminarResponse response = seminarService.updateSeminar(eventManagerId, seminarId, request);
            log.info("Successfully updated seminar ID: {}", seminarId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating seminar: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete a seminar - Event Manager", 
              description = "Delete a seminar. Only accessible by the event manager who created it and only if not approved yet.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Seminar successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an event manager"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the seminar owner or seminar already approved"),
        @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @DeleteMapping("/{seminarId}/delete")
    @PreAuthorize("hasRole('EVENT_MANAGER')")
    public ResponseEntity<Void> deleteSeminar(
            @RequestHeader("X-User-Id") Integer eventManagerId,
            @PathVariable Integer seminarId) {
        log.info("Deleting seminar ID: {} by event manager ID: {}", seminarId, eventManagerId);
        try {
            seminarService.deleteSeminar(eventManagerId, seminarId);
            log.info("Successfully deleted seminar ID: {}", seminarId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting seminar: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get seminar details", 
              description = "Get detailed information about a specific seminar.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved seminar details"),
        @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @GetMapping("/{seminarId}/seminar-details")
    public ResponseEntity<SeminarResponse> getSeminar(
            @PathVariable Integer seminarId) {
        log.info("Getting seminar details for ID: {}", seminarId);
        try {
            SeminarResponse response = seminarService.getSeminar(seminarId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.error("Seminar not found: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get my seminars - Event Manager", 
              description = "Get all seminars created by the current event manager.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved event manager's seminars"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an event manager")
    })
    @GetMapping("/event-manager-seminars-list")
    @PreAuthorize("hasRole('EVENT_MANAGER')")
    public ResponseEntity<List<SeminarResponse>> getSeminarsByEventManager(
            @RequestHeader("X-User-Id") Integer eventManagerId) {
        log.info("Getting all seminars for event manager ID: {}", eventManagerId);
        List<SeminarResponse> seminars = seminarService.getSeminarsByEventManager(eventManagerId);
        return ResponseEntity.ok(seminars);
    }

    @Operation(summary = "Get seminars by status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved seminars by status"),
        @ApiResponse(responseCode = "400", description = "Invalid status provided")
    })
    @GetMapping("/{status}/seminars-by-status")
    public ResponseEntity<List<SeminarResponse>> getSeminarsByStatus(
            @PathVariable String status) {
        log.info("Getting seminars with status: {}", status);
        try {
            Seminar.Status seminarStatus = Seminar.Status.valueOf(status.toUpperCase());
            List<SeminarResponse> seminars = seminarService.getSeminarsByStatus(seminarStatus);
            return ResponseEntity.ok(seminars);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status provided: {}", status);
            throw new SeminarTicketException("Invalid status: " + status);
        }
    }

    @Operation(summary = "Approve or reject seminar - Admin", 
              description = "Approve or reject a seminar request. Only accessible by administrators.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Seminar status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid approval status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an admin"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @PutMapping("/{seminarId}/status-approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeminarResponse> approveSeminar(
            @RequestHeader("X-User-Id") Integer adminId,
            @PathVariable Integer seminarId,
            @RequestParam String status) {
        log.info("Admin ID: {} approving seminar ID: {} with status: {}", adminId, seminarId, status);
        try {
            Seminar.StatusApprove approvalStatus = Seminar.StatusApprove.valueOf(status.toUpperCase());
            SeminarResponse response = seminarService.approveSeminar(adminId, seminarId, approvalStatus);
            log.info("Successfully updated seminar ID: {} approval status to: {}", seminarId, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid approval status provided: {}", status);
            throw new SeminarTicketException("Invalid approval status: " + status);
        }
    }

    @Operation(summary = "Update seminar status - Event Manager", 
              description = "Update the status of an approved seminar (PENDING, ONGOING, COMPLETED). Only accessible by the event manager who created it.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Seminar status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status provided"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an event manager"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the seminar owner or seminar not approved yet"),
        @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @PutMapping("/{seminarId}/status")
    @PreAuthorize("hasRole('EVENT_MANAGER')")
    public ResponseEntity<SeminarResponse> updateStatus(
            @RequestHeader("X-User-Id") Integer eventManagerId,
            @PathVariable Integer seminarId,
            @RequestParam String status) {
        log.info("Event manager ID: {} updating seminar ID: {} status to: {}", eventManagerId, seminarId, status);
        try {
            Seminar.Status seminarStatus = Seminar.Status.valueOf(status.toUpperCase());
            SeminarResponse response = seminarService.updateStatus(eventManagerId, seminarId, seminarStatus);
            log.info("Successfully updated seminar ID: {} status to: {}", seminarId, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status provided: {}", status);
            throw new SeminarTicketException("Invalid status: " + status);
        }
    }

    @Operation(summary = "Get pending seminars - Admin", 
              description = "Get all seminars with pending status. Only accessible by administrators.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pending seminars"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an admin")
    })
    @GetMapping("/pending-list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeminarResponse>> getPendingSeminars() {
        log.info("Getting all pending seminars");
        List<SeminarResponse> seminars = seminarService.getPendingSeminars(Seminar.StatusApprove.PENDING);
        return ResponseEntity.ok(seminars);
    }

    @Operation(summary = "Get approved seminars", 
              description = "Get all approved seminars.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved approved seminars")
    })
    @GetMapping("/approved-list")
    public ResponseEntity<List<SeminarResponse>> getApprovedSeminars() {
        log.info("Getting all approved seminars");
        List<SeminarResponse> seminars = seminarService.getSeminarsByStatus(Seminar.Status.ONGOING);
        return ResponseEntity.ok(seminars);
    }
}
