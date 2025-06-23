package org.swd392.seminars.service;

import org.swd392.seminars.entity.SagaTransaction;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.payload.response.PaymentInitiationResponse;


public interface OrchestratorSagaService {

    PaymentInitiationResponse startBookTicketSaga(Integer userProfileId, SeminarTicketRequest request);

    SagaTransaction findSagaTransactionByPaymentOrderCode(String paymentOrderCode);

}
