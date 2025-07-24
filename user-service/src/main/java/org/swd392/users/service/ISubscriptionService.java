package org.swd392.users.service;

import org.swd392.users.dto.PaymentInitiationResponse;

public interface ISubscriptionService {
    PaymentInitiationResponse upgrade(Long userId);
}
