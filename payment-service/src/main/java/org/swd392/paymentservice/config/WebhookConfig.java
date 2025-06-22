package org.swd392.paymentservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.payos.PayOS;

@Component
@RequiredArgsConstructor
public class WebhookConfig implements CommandLineRunner {


    private final PayOS payOS;

    @Override
    public void run(String... args) throws Exception {

        String webHookUrl = "https://8e6e-2402-800-63a8-b04a-c09f-9862-d003-5411.ngrok-free.app/payment/api/payments/webhook";
        try {
            String result = payOS.confirmWebhook(webHookUrl);
            System.out.println("✅ Webhook confirmed: " + result);
        } catch (Exception e) {
            System.err.println("❌ Failed to register webhook: " + e.getMessage());
        }
    }
}