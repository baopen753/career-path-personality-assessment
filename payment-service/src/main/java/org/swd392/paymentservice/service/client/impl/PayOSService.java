package org.swd392.paymentservice.service.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.swd392.paymentservice.dto.PaymentRequestDTO;
import org.swd392.paymentservice.dto.PaymentResponseDto;

import vn.payos.PayOS;
import org.springframework.stereotype.Service;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;


@Service
@RequiredArgsConstructor
public class PayOSService {

    private final String REDIRECT_URI = "http://localhost:8086";

    @Autowired
    private final PayOS payOS;

    public PaymentLinkData getPaymentInfo(long orderCode) {
        try {
            System.out.println("🔍 Đang truy vấn đơn hàng orderCode = " + orderCode);
            return payOS.getPaymentLinkInformation(orderCode);
        } catch (Exception e) {
            System.err.println("❌ Không tìm thấy đơn hàng: " + orderCode);
            throw new RuntimeException("Lỗi khi lấy thông tin đơn hàng", e);
        }
    }

    public PaymentResponseDto createPaymentLink(PaymentRequestDTO request) {
        try {
            long orderCode = System.currentTimeMillis();

            PaymentData data = PaymentData.builder()
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .orderCode(orderCode)
                    .cancelUrl(REDIRECT_URI + "/payment/api/payments/cancel")
                    .returnUrl(REDIRECT_URI + "/payment/api/payments/success")
                    .build();

            System.out.println("📤 Gửi thanh toán: orderCode = " + orderCode + ", amount = " + request.getAmount());

            String url = payOS.createPaymentLink(data).getCheckoutUrl();

            return PaymentResponseDto.builder()
                    .checkoutUrl(url)
                    .orderCode(orderCode)
                    .build();

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gọi PayOS: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo link thanh toán PayOS", e);
        }
    }

    public PaymentLinkData cancelPayment(long orderCode, String reason) {
        try {
            return payOS.cancelPaymentLink(orderCode, reason);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hủy đơn hàng", e);
        }
    }

    public WebhookData verifyWebhook(Webhook data) {
        try {
            WebhookData webhookData = data.getData(); // ✅ Lấy trực tiếp

            System.out.println("📩 Webhook nhận được:");
            System.out.println("👉 Mã đơn: " + webhookData.getOrderCode());
            System.out.println("👉 Trạng thái: " + webhookData.getCode());
            System.out.println("👉 Mô tả: " + webhookData.getDesc());

            return webhookData;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xử lý webhook test", e);
        }
    }


}
