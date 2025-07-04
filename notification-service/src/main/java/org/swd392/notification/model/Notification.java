//package org.swd392.notification.model;
//
//import lombok.Data;
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@Table(name = "notifications")
//public class Notification {
//    @jakarta.persistence.Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String userId;
//
//    @Column(nullable = false)
//    private String title;
//
//    @Column(nullable = false, length = 1000)
//    private String message;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private NotificationType type;
//
//    @Column(nullable = false)
//    private boolean isRead;
//
//    @Column(nullable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "related_entity_id")
//    private String relatedEntityId;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private NotificationStatus status;
//}