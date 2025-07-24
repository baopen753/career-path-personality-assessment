package org.swd392.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swd392.users.entity.subscription.Subscription;
import org.swd392.users.entity.subscription.UserSubscriptionId;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, UserSubscriptionId> {
    Optional<Subscription> findSubscriptionByPaymentOrderCode(String paymentOrderCode);
}
