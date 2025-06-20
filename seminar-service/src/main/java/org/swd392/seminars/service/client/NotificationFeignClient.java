package org.swd392.seminars.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification", url = "${notification.service.url}")
public interface NotificationFeignClient {
    
    @PostMapping("/api/notifications/ticket-confirmation")
    ResponseEntity<Void> sendTicketConfirmation(
            @RequestParam String attendeeEmail,
            @RequestParam String attendeeName,
            @RequestParam String ticketId,
            @RequestParam String seminarName,
            @RequestParam String seminarDate,
            @RequestParam String seminarLocation,
            @RequestParam String attendeeType,
            @RequestParam String ticketLink);
    
    @PostMapping("/api/notifications/seminar-status")
    ResponseEntity<Void> sendSeminarStatusNotification(
            @RequestParam String eventManagerEmail,
            @RequestParam String eventManagerName,
            @RequestParam String seminarName,
            @RequestParam String status,
            @RequestParam(required = false) String rejectionReason,
            @RequestParam String seminarDate,
            @RequestParam String seminarTime,
            @RequestParam String seminarLocation,
            @RequestParam String seminarLink);
}