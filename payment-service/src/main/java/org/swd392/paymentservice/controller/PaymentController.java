package org.swd392.paymentservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.paymentservice.dto.PaymentCallbackEvent;
import org.swd392.paymentservice.dto.PaymentRequestDTO;
import org.swd392.paymentservice.dto.PaymentResponseDto;
import org.swd392.paymentservice.dto.ResponseDTO;
import org.swd392.paymentservice.service.client.SeminarFeignClient;
import org.swd392.paymentservice.service.client.impl.PayOSService;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PayOSService payOSService;
    private final SeminarFeignClient seminarFeignClient;

    public PaymentController(PayOSService payOSService, SeminarFeignClient seminarFeignClient) {
        this.payOSService = payOSService;
        this.seminarFeignClient = seminarFeignClient;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDTO request) {

        PaymentResponseDto responsePayment;
        try {
            responsePayment = payOSService.createPaymentLink(request);
            return ResponseEntity.ok(responsePayment);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/info/{orderCode}")
    public ResponseEntity<?> getPaymentInfo(@PathVariable long orderCode) {
        return ResponseEntity.ok(payOSService.getPaymentInfo(orderCode));
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestParam long orderCode, @RequestParam String reason) {

        PaymentCallbackEvent paymentCallbackEvent = PaymentCallbackEvent.builder()
                .paymentOrderCode(orderCode)
                .success(false)
                .message(reason)
                .build();

        seminarFeignClient.handlePaymentCallback(paymentCallbackEvent);
        return ResponseEntity.ok(payOSService.cancelPayment(orderCode, reason));
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> handleCancelRedirect(@RequestParam long orderCode) {
        return ResponseEntity.ok(payOSService.cancelPayment(orderCode, "User clicked cancel !!"));
    }

    @GetMapping("/success")
    public ResponseEntity<ResponseDTO<String>> handleSucceedRedirect(@RequestParam Long orderCode) {
        return ResponseEntity.ok(ResponseDTO.<String>builder()
                .status(200)
                .data(orderCode.toString())
                .message("Payment successfully")
                .build());
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Webhook webhook) {
        log.info("📩 Received PayOS webhook: {}", webhook);

        // Defensive: check for test/verification POST
        if (webhook == null) {
            log.info("Received test/verification webhook from PayOS. Ignoring.");
            return ResponseEntity.ok("Test webhook received");
        }

        WebhookData data = payOSService.verifyWebhook(webhook);

        Long orderCode = data.getOrderCode();
        String code = data.getCode();

        System.out.println("✅ Webhook xác minh OK | Mã đơn: " + data.getOrderCode() +
                " | Mã trạng thái: " + data.getCode() +
                " | Mô tả: " + data.getDesc());

        try {
            boolean isSuccess = "00".equals(code); // PayOS success code
            String message = isSuccess ? "Payment successful" : data.getDesc();

            PaymentCallbackEvent paymentCallbackEvent = PaymentCallbackEvent.builder()
                    .paymentOrderCode(orderCode)
                    .success(isSuccess)
                    .message(message)
                    .build();

            seminarFeignClient.handlePaymentCallback(paymentCallbackEvent);
            log.info("✅ Notified seminar service about payment status for order: {}", orderCode);
        } catch (Exception e) {
            log.error("❌ Failed to notify seminar service about payment status for order: {}", orderCode, e);
        }
        return ResponseEntity.ok("OK");
    }
}