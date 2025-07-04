//package org.swd392.notification.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import org.swd392.notification.model.Notification;
//import java.util.List;
//
//@Repository
//public interface NotificationRepository extends JpaRepository<Notification, Long> {
//    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
//}