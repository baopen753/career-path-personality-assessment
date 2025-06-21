package org.swd392.seminars.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.seminars.dto.PaymentRequestDTO;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.service.OrchestratorSagaService;
import org.swd392.seminars.service.SeminarTicketService;
import org.swd392.seminars.service.client.PaymentFeignClient;

@Service
@RequiredArgsConstructor
public class OrchestratorSagaServiceImpl implements OrchestratorSagaService {

    private final PaymentFeignClient paymentFeignClient;
    private final SeminarTicketService seminarTicketService;

    @Override
    @Transactional
    public void startBookTicketSaga(Integer userProfileId, SeminarTicketRequest request) {

        try {
            request.setUserProfileId(userProfileId);

            // booking transaction
            seminarTicketService.bookTicket(request);

            // payment transaction
            paymentFeignClient.createPayment(convertToPaymentRequestDto(request));

        } catch (Exception e) {
            compensateBookingSage(request);
        }

    }

    private PaymentRequestDTO convertToPaymentRequestDto(SeminarTicketRequest seminarTicketRequest) {
        PaymentRequestDTO paymentRequestDTO = PaymentRequestDTO.builder()
                .amount(seminarTicketRequest.getPrice())
                .description(seminarTicketRequest.getDescription())
                .build();
        return paymentRequestDTO;
    }

    private void compensateBookingSage(SeminarTicketRequest seminarTicketRequest) {
        Integer userProfileId = seminarTicketRequest.getUserProfileId();
        Integer seminarId = seminarTicketRequest.getSeminarId();
        seminarTicketService.deleteBookedTicket(seminarId, userProfileId);
    }
}
