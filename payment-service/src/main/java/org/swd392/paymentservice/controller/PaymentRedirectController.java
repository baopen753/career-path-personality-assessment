package org.swd392.paymentservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PaymentRedirectController {

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @GetMapping(value = "/api/payments/cancel", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String handlePaymentCancel(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cancel,
            @RequestParam(required = false) String code
    ) {
        StringBuilder redirectUrl = new StringBuilder(frontendUrl + "/seminars?");
        if (id != null) redirectUrl.append("id=").append(id).append("&");
        if (orderCode != null) redirectUrl.append("orderCode=").append(orderCode).append("&");
        if (status != null) redirectUrl.append("status=").append(status).append("&");
        if (cancel != null) redirectUrl.append("cancel=").append(cancel).append("&");
        if (code != null) redirectUrl.append("code=").append(code).append("&");

        // Remove trailing '&' if present
        if (redirectUrl.charAt(redirectUrl.length() - 1) == '&') {
            redirectUrl.deleteCharAt(redirectUrl.length() - 1);
        }

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Payment Cancelled - Redirecting...</title>" +
                "<meta http-equiv='refresh' content='0; url=" + redirectUrl.toString() + "'>" +
                "</head>" +
                "<body>" +
                "<p>Payment cancelled. Redirecting to seminars page...</p>" +
                "<script>window.location.href = '" + redirectUrl.toString() + "';</script>" +
                "</body>" +
                "</html>";
    }

    @GetMapping(value = "/api/payments/success", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String handlePaymentSuccess(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String code
    ) {
        StringBuilder redirectUrl = new StringBuilder(frontendUrl + "/seminars?");
        if (id != null) redirectUrl.append("id=").append(id).append("&");
        if (orderCode != null) redirectUrl.append("orderCode=").append(orderCode).append("&");
        if (status != null) redirectUrl.append("status=").append(status).append("&");
        if (code != null) redirectUrl.append("code=").append(code).append("&");

        // Remove trailing '&' if present
        if (redirectUrl.charAt(redirectUrl.length() - 1) == '&') {
            redirectUrl.deleteCharAt(redirectUrl.length() - 1);
        }

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Payment Successful - Redirecting...</title>" +
                "<meta http-equiv='refresh' content='0; url=" + redirectUrl.toString() + "'>" +
                "</head>" +
                "<body>" +
                "<p>Payment successful! Redirecting to seminars page...</p>" +
                "<script>window.location.href = '" + redirectUrl.toString() + "';</script>" +
                "</body>" +
                "</html>";
    }
}
