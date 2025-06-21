package org.swd392.paymentservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.paymentservice.dto.PaymentRequestDTO;
import org.swd392.paymentservice.dto.PaymentResponseDto;
import org.swd392.paymentservice.dto.ResponseDTO;
import org.swd392.paymentservice.service.client.impl.PayOSService;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PayOSService payOSService;

    public PaymentController(PayOSService payOSService) {
        this.payOSService = payOSService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<PaymentResponseDto>> createPayment(@RequestBody PaymentRequestDTO request) {

        PaymentResponseDto responsePayment;
        try {
            responsePayment = payOSService.createPaymentLink(request);
            return ResponseEntity.ok(ResponseDTO.<PaymentResponseDto>builder()
                    .status(200)
                    .message("Create Payment Link successfully")
                    .data(responsePayment)
                    .build()
            );
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

    //    @PostMapping("/webhook")
//    public ResponseEntity<String> handleWebhook(@RequestBody Webhook webhook) {
//        WebhookData data = payOSService.verifyWebhook(webhook);
//
//        System.out.println("✅ Webhook xác minh OK | Mã đơn: " + data.getOrderCode() +
//                " | Mã trạng thái: " + data.getCode() +
//                " | Mô tả: " + data.getDesc());
//
//        return ResponseEntity.ok("OK");
//    }


    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Webhook webhook) {

        log.info("📩 Received PayOS webhook: {}", webhook);
        WebhookData data = payOSService.verifyWebhook(webhook);

        System.out.println("✅ Webhook xác minh OK | Mã đơn: " + data.getOrderCode() +
                " | Mã trạng thái: " + data.getCode() +
                " | Mô tả: " + data.getDesc());

        return ResponseEntity.ok("OK");
    }

}
