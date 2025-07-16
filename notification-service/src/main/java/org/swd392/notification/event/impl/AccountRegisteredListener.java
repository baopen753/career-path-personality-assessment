package org.swd392.notification.event.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.swd392.notification.event.AccountRegisteredEvent;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AccountRegisteredListener {
    private final JavaMailSender mailSender;
    private final FreeMarkerConfigurer freemarkerConfig;

    public AccountRegisteredListener(JavaMailSender mailSender, FreeMarkerConfigurer freemarkerConfig) {
        this.mailSender = mailSender;
        this.freemarkerConfig = freemarkerConfig;
    }

    
    public void handleAccountRegistered(AccountRegisteredEvent event) {
        log.info("Received AccountRegisteredEvent: {}", event);
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("userName", event.getUserName());
            model.put("username", event.getUsername());
            model.put("accountType", event.getAccountType());
            model.put("registrationDate", event.getRegistrationDate());
            model.put("loginLink", event.getLoginLink());

            sendEmail(event.getUserEmail(), "Welcome to Career Path System", "account-registration.ftl", model);
            log.info("Successfully sent registration confirmation email to: {}", event.getUserEmail());
        } catch (Exception e) {
            log.error("Failed to send registration confirmation email to: {}. Error: {}", event.getUserEmail(), e.getMessage());
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