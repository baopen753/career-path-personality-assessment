package org.swd392.seminars.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.service.OrchestratorSagaService;
import org.swd392.seminars.service.SeminarTicketService;
import org.swd392.seminars.service.client.PaymentFeignClient;

//@Service
//@RequiredArgsConstructor
//public class OrchestratorSagaServiceImpl implements OrchestratorSagaService {
//
//    private final PaymentFeignClient paymentFeignClient;
//    private final SeminarTicketService seminarTicketService;
//
//    @Override
//    public void startOrderSaga(Integer userProfileId, SeminarTicketRequest request) {
//
//        try {
//            request.setUserProfileId(userProfileId);
//
//            // booking transaction
//            seminarTicketService.bookTicket(request);
//
//            // payment transaction
//            paymentFeignClient.createPayment
//        }
//
//    }
//}
