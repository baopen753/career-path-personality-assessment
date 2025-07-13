package org.swd392.users.service.impl;

import org.swd392.users.entity.subscription.Subscription;

import java.util.Optional;

public interface ISubscriptionService {
    Optional<Subscription> getSubscriptionById(Long subscriptionId);
}
