package org.swd392.paymentservice.service.handler;

import org.swd392.paymentservice.dto.PaymentCallbackEvent;
import org.swd392.paymentservice.enums.PaymentFlow;

public interface PaymentCallbackHandler {

    boolean supports(PaymentFlow flowType);
    void handle(PaymentCallbackEvent event);

}
