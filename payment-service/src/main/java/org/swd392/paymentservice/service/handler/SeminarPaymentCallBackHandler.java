package org.swd392.paymentservice.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swd392.paymentservice.dto.PaymentCallbackEvent;
import org.swd392.paymentservice.enums.PaymentFlow;
import org.swd392.paymentservice.service.client.SeminarFeignClient;

@Service
@RequiredArgsConstructor
public class SeminarPaymentCallBackHandler implements PaymentCallbackHandler {

    private SeminarFeignClient seminarFeignClient;

    @Override
    public boolean supports(PaymentFlow flowType) {
        return flowType.equals(PaymentFlow.SEMINAR_TICKET);
    }

    @Override
    public void handle(PaymentCallbackEvent callbackEvent) {
        seminarFeignClient.handlePaymentCallback(callbackEvent);
    }

}