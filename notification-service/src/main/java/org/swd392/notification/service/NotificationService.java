//package org.swd392.notification.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
//import org.swd392.notification.model.Notification;
//import org.swd392.notification.model.NotificationStatus;
//import org.swd392.notification.model.NotificationType;
//import org.swd392.notification.repository.NotificationRepository;
//import freemarker.template.Template;
//import jakarta.mail.internet.MimeMessage;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class NotificationService {
//    private final JavaMailSender mailSender;
//    private final FreeMarkerConfigurer freemarkerConfig;
//    private final NotificationRepository notificationRepository;
//
//    public void sendSeminarStatusNotification(String eventManagerEmail, String eventManagerName,
//            String seminarName, String status, String rejectionReason, String seminarDate,
//            String seminarTime, String seminarLocation, String seminarLink) {
//        try {
//            // Create email context
//            Map<String, Object> model = new HashMap<>();
//            model.put("eventManagerName", eventManagerName);
//            model.put("seminarName", seminarName);
//            model.put("status", status);
//            model.put("rejectionReason", rejectionReason);
//            model.put("seminarDate", seminarDate);
//            model.put("seminarTime", seminarTime);            model.put("seminarLocation", seminarLocation);
//            model.put("seminarLink", seminarLink);
//
//            // Send email
//            sendEmail(eventManagerEmail, "Seminar Status Update", "seminar-status-notification.ftl", model);
//
//            // Create in-app notification
//            Notification notification = new Notification();
//            notification.setUserId(eventManagerEmail);
//            notification.setTitle("Seminar Status Update");
//            notification.setMessage(String.format("Your seminar '%s' has been %s", seminarName, status));
//            notification.setType(NotificationType.SEMINAR_STATUS);
//            notification.setCreatedAt(LocalDateTime.now());
//            notification.setStatus(NotificationStatus.SENT);
//            notificationRepository.save(notification);
//
//        } catch (Exception e) {
//            log.error("Error sending seminar status notification", e);
//            throw new RuntimeException("Failed to send seminar status notification", e);
//        }
//    }
//
//    public void sendTicketConfirmation(String attendeeEmail, String attendeeName, String ticketId,
//            String seminarName, String seminarDate, String seminarLocation,
//            String attendeeType, String ticketLink) {
//        try {
//            // Create email context
//            Map<String, Object> model = new HashMap<>();
//            model.put("attendeeName", attendeeName);
//            model.put("ticketId", ticketId);
//            model.put("seminarName", seminarName);
//            model.put("seminarDate", seminarDate);
//            model.put("seminarLocation", seminarLocation);
//            model.put("userRole", attendeeType); // Map attendeeType to userRole for template
//            model.put("attendeeType", attendeeType); // Keep both for backward compatibility
//            model.put("ticketLink", ticketLink);
//
//            log.info("Preparing to send ticket confirmation email to: {}", attendeeEmail);
//            log.debug("Email template data: {}", model);
//
//            // Send email
//            sendEmail(attendeeEmail, "Seminar Ticket Confirmation", "seminar-ticket-confirmation.ftl", model);
//
//            // Create in-app notification
//            Notification notification = new Notification();
//            notification.setUserId(attendeeEmail);
//            notification.setTitle("Ticket Confirmation");
//            notification.setMessage(String.format("Your ticket for '%s' has been confirmed", seminarName));
//            notification.setType(NotificationType.TICKET_CONFIRMATION);
//            notification.setCreatedAt(LocalDateTime.now());
//            notification.setStatus(NotificationStatus.SENT);
//            notificationRepository.save(notification);
//
//            log.info("Successfully sent ticket confirmation email to: {}", attendeeEmail);
//
//        } catch (Exception e) {
//            log.error("Error sending ticket confirmation email to: {}. Error: {}", attendeeEmail, e.getMessage());
//            log.debug("Full stack trace:", e);
//            throw new RuntimeException("Failed to send ticket confirmation email", e);
//        }
//    }
//
//    public void sendAccountRegistrationConfirmation(String userEmail, String userName, String username,
//            String accountType, String registrationDate, String loginLink) {
//        try {
//            // Create email context
//            Map<String, Object> model = new HashMap<>();
//            model.put("userName", userName);
//            model.put("username", username);
//            model.put("accountType", accountType);            model.put("registrationDate", registrationDate);
//            model.put("loginLink", loginLink);
//
//            // Send email
//            sendEmail(userEmail, "Welcome to Career Path System", "account-registration.ftl", model);
//
//            // Create in-app notification
//            Notification notification = new Notification();
//            notification.setUserId(userEmail);
//            notification.setTitle("Account Registration Successful");
//            notification.setMessage("Your account has been successfully created");
//            notification.setType(NotificationType.ACCOUNT_REGISTRATION);
//            notification.setCreatedAt(LocalDateTime.now());
//            notification.setStatus(NotificationStatus.SENT);
//            notificationRepository.save(notification);
//
//        } catch (Exception e) {
//            log.error("Error sending account registration confirmation", e);
//            throw new RuntimeException("Failed to send account registration confirmation", e);        }
//    }
//
//    public List<Notification> getUserNotifications(String userId) {
//        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
//    }
//
//    public void markNotificationAsRead(String notificationId) {
//        notificationRepository.findById(Long.parseLong(notificationId)).ifPresent(notification -> {
//            notification.setRead(true);
//            notificationRepository.save(notification);
//        });
//    }
//
//    private void sendEmail(String to, String subject, String templateName, Map<String, Object> model) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            Template template = freemarkerConfig.getConfiguration().getTemplate(templateName);
//            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
//
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//            log.info("Successfully sent email to: {}", to);
//        } catch (Exception e) {
//            log.error("Failed to send email to: {}. See full stack trace for details.", to, e);
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }
//}