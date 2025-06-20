package org.swd392.paymentservice.service.client.impl;

import org.swd392.paymentservice.dto.PaymentRequestDTO;
import vn.payos.PayOS;
import org.springframework.stereotype.Service;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayOSService {

    private final PayOS payOS;

    public PayOSService() {
        this.payOS = new PayOS(
                "d29b3b39-37ab-4f39-9f71-67a6c2aa115e",
                "6efcfc9b-b0cd-454e-858b-1cecc8820362",
                "3869704876d05fb921ee674e6abd079ea45788a9e15e72a63c7c7ef88c54b28f"
        );
    }

    public PaymentLinkData getPaymentInfo(long orderCode) {
        try {
            System.out.println("🔍 Đang truy vấn đơn hàng orderCode = " + orderCode);
            return payOS.getPaymentLinkInformation(orderCode);
        } catch (Exception e) {
            System.err.println("❌ Không tìm thấy đơn hàng: " + orderCode);
            throw new RuntimeException("Lỗi khi lấy thông tin đơn hàng", e);
        }
    }

    public Map<String, Object> createPaymentLink(PaymentRequestDTO request) {
        try {
            long orderCode = System.currentTimeMillis();

            PaymentData data = PaymentData.builder()
                    .amount((int) request.getAmount())
                    .description(request.getDescription())
                    .orderCode(orderCode)
                    .cancelUrl("https://your-site.com/cancel")
                    .returnUrl("https://your-site.com/success")
                    .build();

            System.out.println("📤 Gửi thanh toán: orderCode = " + orderCode + ", amount = " + request.getAmount());

            String url = payOS.createPaymentLink(data).getCheckoutUrl();

            Map<String, Object> result = new HashMap<>();
            result.put("checkoutUrl", url);
            result.put("orderCode", orderCode);
            return result;

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
