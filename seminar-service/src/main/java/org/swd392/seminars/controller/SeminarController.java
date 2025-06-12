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
import org.swd392.seminars.payload.request.SeminarRequest;
import org.swd392.seminars.payload.response.SeminarResponse;
import org.swd392.seminars.service.SeminarService;

import java.util.List;
import java.util.UUID;

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
    @PostMapping("/create")
    @PreAuthorize("hasRole('EVENT_MANAGER')")
    public ResponseEntity<SeminarResponse> createSeminar(
            @RequestHeader("X-User-Id") Integer eventManagerId,
            @RequestBody SeminarRequest request) {
        return new ResponseEntity<>(seminarService.createSeminar(eventManagerId, request), HttpStatus.CREATED);
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
    @PutMapping("/{seminarId}/update")
    @PreAuthorize("hasRole('EVENT_MANAGER')")
    public ResponseEntity<SeminarResponse> updateSeminar(
            @RequestHeader("X-User-Id") Integer eventManagerId,
            @PathVariable Integer seminarId,
            @RequestBody SeminarRequest request) {
        return ResponseEntity.ok(seminarService.updateSeminar(eventManagerId, seminarId, request));
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
    @PutMapping("/{seminarId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeminarResponse> approveSeminar(
            @RequestHeader("X-User-Id") Integer adminId,
            @PathVariable Integer seminarId,
            @RequestParam String status) {  // APPROVED, REJECTED
        return ResponseEntity.ok(seminarService.approveSeminar(adminId, seminarId, status));
    }

    @Operation(summary = "Get pending seminars - Admin", 
              description = "Get all seminars pending approval. Only accessible by administrators.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pending seminars"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an admin"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeminarResponse>> getPendingSeminars() {
        return ResponseEntity.ok(seminarService.getSeminarsByStatus("PENDING"));
    }

    @Operation(summary = "Get my seminars - Event Manager", 
              description = "Get all seminars created by the current event manager.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved event manager's seminars"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an event manager")
    })
    @GetMapping("/my-seminars")
    @PreAuthorize("hasRole('EVENT_MANAGER')")
    public ResponseEntity<List<SeminarResponse>> getMySeminars(
            @RequestHeader("X-User-Id") Integer eventManagerId) {
        return ResponseEntity.ok(seminarService.getSeminarsByEventManager(eventManagerId));
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
        seminarService.deleteSeminar(eventManagerId, seminarId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get approved seminars", 
              description = "Get all approved seminars. Accessible by all authenticated users.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved approved seminars")
    })
    @GetMapping("/approved")
    public ResponseEntity<List<SeminarResponse>> getApprovedSeminars() {
        return ResponseEntity.ok(seminarService.getSeminarsByStatus("APPROVED"));
    }

    @Operation(summary = "Get seminar details", 
              description = "Get detailed information about a specific seminar.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved seminar details"),
        @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @GetMapping("/{seminarId}/details")
    public ResponseEntity<SeminarResponse> getSeminar(
            @PathVariable Integer seminarId) {
        return ResponseEntity.ok(seminarService.getSeminar(seminarId));
    }

    @Operation(summary = "Update seminar video link - Event Manager", 
              description = "Update the video recording link for a completed seminar. Only accessible by event managers.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Video link successfully updated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an event manager"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the seminar owner"),
        @ApiResponse(responseCode = "404", description = "Seminar not found")
    })
    @PutMapping("/{seminarId}/video")
    @PreAuthorize("hasRole('EVENT_MANAGER')")
    public ResponseEntity<SeminarResponse> updateVideoLink(
            @RequestHeader("X-User-Id") Integer eventManagerId,
            @PathVariable Integer seminarId,
            @RequestParam String videoUrl) {
        return ResponseEntity.ok(seminarService.updateVideoLink(eventManagerId, seminarId, videoUrl));
    }
}
