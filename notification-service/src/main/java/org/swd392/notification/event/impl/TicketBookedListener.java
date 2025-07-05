package org.swd392.notification.event.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.swd392.notification.event.EventListener;
import org.swd392.notification.event.TicketBookedEvent;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TicketBookedListener implements EventListener<TicketBookedEvent> {
    private final JavaMailSender mailSender;
    private final FreeMarkerConfigurer freemarkerConfig;

    public TicketBookedListener(JavaMailSender mailSender, FreeMarkerConfigurer freemarkerConfig) {
        this.mailSender = mailSender;
        this.freemarkerConfig = freemarkerConfig;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    @Override
    public void consume(TicketBookedEvent event) {
        log.info("TicketBookedEvent received: {}", event);
        // LOG INFO: TicketBookedEvent received: TicketBookedEvent(userId=7, email=[user-email], fullName=...., paymentOrderCode=1751594894132, status=COMPLETED, createdAt=2025-07-04T10:38:09.389508823)

        // TODO: gửi email thông email dưới đây đi kèm thông tin liên quan đến transaction


        try {
            // Create email context
            Map<String, Object> model = new HashMap<>();
            model.put("email", event.getEmail());
            model.put("fullName", event.getFullName());
            model.put("paymentOrderCode", event.getPaymentOrderCode());
            model.put("status", event.getStatus());
            model.put("createdAt", event.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            // Add amount and payment method if available
            if (event.getAmount() != null) {
                model.put("amount", event.getAmount());
            }
            if (event.getPaymentMethod() != null) {
                model.put("paymentMethod", event.getPaymentMethod());
            }

            log.info("Preparing to send ticket booking confirmation email to: {}", event.getEmail());
            log.debug("Email template data: {}", model);

            // Send email
            sendEmail(event.getEmail(), "Ticket Booking Confirmation", "ticket-booking-confirmation.ftl", model);

            log.info("Successfully sent ticket booking confirmation email to: {}", event.getEmail());

        } catch (Exception e) {
            log.error("Error sending ticket booking confirmation email to: {}. Error: {}", event.getEmail(), e.getMessage());
            log.debug("Full stack trace:", e);
        }
    }

    private void sendEmail(String to, String subject, String templateName, Map<String, Object> model) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Template template = freemarkerConfig.getConfiguration().getTemplate(templateName);
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent email to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}. See full stack trace for details.", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
