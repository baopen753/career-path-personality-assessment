package org.swd392.paymentservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.paymentservice.dto.PaymentRequestDTO;
import org.swd392.paymentservice.service.client.impl.PayOSService;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PayOSService payOSService;

    public PaymentController(PayOSService payOSService) {
        this.payOSService = payOSService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody PaymentRequestDTO request) {
        Map<String, Object> result = payOSService.createPaymentLink(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/info/{orderCode}")
    public ResponseEntity<?> getPaymentInfo(@PathVariable long orderCode) {
        return ResponseEntity.ok(payOSService.getPaymentInfo(orderCode));
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestParam long orderCode, @RequestParam String reason) {
        return ResponseEntity.ok(payOSService.cancelPayment(orderCode, reason));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Webhook webhook) {
        WebhookData data = payOSService.verifyWebhook(webhook);

        System.out.println("✅ Webhook xác minh OK | Mã đơn: " + data.getOrderCode() +
                " | Mã trạng thái: " + data.getCode() +
                " | Mô tả: " + data.getDesc());

        return ResponseEntity.ok("OK");
    }
}
