package org.swd392.seminars.service;

import org.swd392.seminars.payload.request.SeminarTicketRequest;

public interface OrchestratorSagaService {

    void startBookTicketSaga(Integer userProfileId, SeminarTicketRequest request);

}
