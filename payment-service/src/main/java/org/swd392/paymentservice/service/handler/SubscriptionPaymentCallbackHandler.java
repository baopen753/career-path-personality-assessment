package org.swd392.paymentservice.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swd392.paymentservice.dto.PaymentCallbackEvent;
import org.swd392.paymentservice.enums.PaymentFlow;
import org.swd392.paymentservice.service.client.UserFeignClient;

@Service
@RequiredArgsConstructor
public class SubscriptionPaymentCallbackHandler implements PaymentCallbackHandler {

    private UserFeignClient userFeignClient;

    @Override
    public boolean supports(PaymentFlow flowType) {
        return flowType.equals(PaymentFlow.SUBSCRIPTION);
    }

    @Override
    public void handle(PaymentCallbackEvent callbackEvent) {
        userFeignClient.handlePaymentCallback(callbackEvent);
    }
}
