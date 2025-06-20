package org.swd392.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.notification.model.Notification;
import org.swd392.notification.service.NotificationService;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "APIs for managing notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all notifications for a user")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable String notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seminar-status")
    @Operation(summary = "Send seminar status notification")
    public ResponseEntity<Void> sendSeminarStatusNotification(
            @RequestParam String eventManagerEmail,
            @RequestParam String eventManagerName,
            @RequestParam String seminarName,
            @RequestParam String status,
            @RequestParam(required = false) String rejectionReason,
            @RequestParam String seminarDate,
            @RequestParam String seminarTime,
            @RequestParam String seminarLocation,
            @RequestParam String seminarLink) {
        notificationService.sendSeminarStatusNotification(
                eventManagerEmail, eventManagerName, seminarName, status,
                rejectionReason, seminarDate, seminarTime, seminarLocation, seminarLink);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ticket-confirmation")
    @Operation(summary = "Send ticket confirmation notification")
    public ResponseEntity<Void> sendTicketConfirmation(
            @RequestParam String attendeeEmail,
            @RequestParam String attendeeName,
            @RequestParam String ticketId,
            @RequestParam String seminarName,
            @RequestParam String seminarDate,
            @RequestParam String seminarLocation,
            @RequestParam String attendeeType,
            @RequestParam String ticketLink) {
        notificationService.sendTicketConfirmation(
                attendeeEmail, attendeeName, ticketId, seminarName,
                seminarDate, seminarLocation, attendeeType, ticketLink);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/account-registration")
    @Operation(summary = "Send account registration confirmation")
    public ResponseEntity<Void> sendAccountRegistrationConfirmation(
            @RequestParam String userEmail,
            @RequestParam String userName,
            @RequestParam String username,
            @RequestParam String accountType,
            @RequestParam String registrationDate,
            @RequestParam String loginLink) {
        notificationService.sendAccountRegistrationConfirmation(
                userEmail, userName, username, accountType, registrationDate, loginLink);
        return ResponseEntity.ok().build();
    }
} 