package org.swd392.notification.event.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.swd392.notification.event.SeminarApprovedEvent;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SeminarApprovedListener {
    private final JavaMailSender mailSender;
    private final FreeMarkerConfigurer freemarkerConfig;

    public SeminarApprovedListener(JavaMailSender mailSender, FreeMarkerConfigurer freemarkerConfig) {
        this.mailSender = mailSender;
        this.freemarkerConfig = freemarkerConfig;
    }

    @RabbitListener(queues = "${rabbitmq.seminar-approved.queue}")
    public void consume(SeminarApprovedEvent event) {
        log.info("SeminarApprovedEvent received: {}", event);
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("managerEmail", event.getManagerEmail());
            model.put("managerFullName", event.getManagerFullName());
            model.put("seminarTitle", event.getSeminarTitle());
            model.put("approvedAt", event.getApprovedAt());
            model.put("statusApprove", event.getStatusApprove());

            sendEmail(event.getManagerEmail(), "Seminar Approved Notification", "seminar-status-notification.ftl", model);
            log.info("Successfully sent seminar approved notification email to: {}", event.getManagerEmail());
        } catch (Exception e) {
            log.error("Error sending seminar approved notification email to: {}. Error: {}", event.getManagerEmail(), e.getMessage());
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
            helper.setFrom("anhphamle2002@gmail.com", "Career Path System");
            mailSender.send(message);
            log.info("Successfully sent email to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}. See full stack trace for details.", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
} 