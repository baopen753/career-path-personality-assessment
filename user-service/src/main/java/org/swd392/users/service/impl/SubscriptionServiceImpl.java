package org.swd392.users.service.impl;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.users.dto.PaymentInitiationResponse;
import org.swd392.users.dto.PaymentRequestDTO;
import org.swd392.users.entity.Package;
import org.swd392.users.entity.PackageType;
import org.swd392.users.entity.User;
import org.swd392.users.entity.subscription.Subscription;
import org.swd392.users.entity.subscription.UserSubscriptionId;
import org.swd392.users.event.PaymentCallbackEvent;
import org.swd392.users.exception.BadRequestException;
import org.swd392.users.exception.ServiceUnavailableException;
import org.swd392.users.exception.SubscriptionNotFoundException;
import org.swd392.users.exception.UserNotFoundException;
import org.swd392.users.repository.PackageRepository;
import org.swd392.users.repository.SubscriptionRepository;
import org.swd392.users.service.ISubscriptionService;
import org.swd392.users.service.client.PaymentFeignClient;

import java.time.LocalDateTime;

@Slf4j
@Service
public class SubscriptionServiceImpl implements ISubscriptionService {

    private final IUserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentFeignClient paymentFeignClient;
    private final PackageRepository packageRepository;

    public SubscriptionServiceImpl(IUserService userService, SubscriptionRepository subscriptionRepository,
            PaymentFeignClient paymentFeignClient, PackageRepository packageRepository) {
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentFeignClient = paymentFeignClient;
        this.packageRepository = packageRepository;
    }

    @Transactional
    @Override
    public PaymentInitiationResponse upgrade(Long userId) {

        User userInDb = userService.getUserById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + userId));

        // check current package type, only upgrade for STANDARD --> PREMIUM
        if (!userInDb.getCurrentPackage().equalsIgnoreCase("standard"))
            throw new BadRequestException("Cannot upgrade! You're in an another subscription");

        Package packageInDb = packageRepository.findPackageByPackageName(PackageType.PREMIUM.name());

        PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                .amount(packageInDb.getPrice())
                .description("Subscription amount: " + packageInDb.getPrice().intValue())
                .build();

        try {
            var response = paymentFeignClient.createPayment(paymentRequest);

            Subscription newSubscription = createSubscription(userId, packageInDb.getPackageId());
            newSubscription.setUser(userInDb);
            newSubscription.setPackageType(packageInDb);
            newSubscription.setPaymentOrderCode(String.valueOf(response.getBody().getOrderCode()));

            // Payment successful
            subscriptionRepository.save(newSubscription);

            // Extract payment details
            String paymentOrderCode = String
                    .valueOf(response.getBody() != null ? response.getBody().getOrderCode() : null);
            String checkoutUrl = response.getBody() != null ? response.getBody().getCheckoutUrl() : null;

            log.info("Payment initiated for upgrading subscription: payment order code: {}, checkout URL: {}",
                    paymentOrderCode, checkoutUrl);

            // Return payment initiation response to client
            return PaymentInitiationResponse.builder()
                    .orderCode(paymentOrderCode)
                    .checkoutUrl(checkoutUrl)
                    .message("Payment initiated successfully. Please complete payment using the provided URL.")
                    .build();

        } catch (FeignException e) {
            if (e.getMessage().contains("Load balancer does not contain an instance for the service payment")) {
                throw new ServiceUnavailableException("Payment service unavailable. Please try again later.");
            }
            throw new ServiceUnavailableException("Error occurred while creating payment: " + e.getMessage());
        }
    }

    @Transactional
    @EventListener // succeed message only sent after transaction commits
    public void handlePaymentCallback(PaymentCallbackEvent event) {
        log.info("Received payment callback for payment order code: {}, success: {}",
                event.getPaymentOrderCode(), event.isSuccess());

        Subscription subscription = subscriptionRepository
                .findSubscriptionByPaymentOrderCode(event.getPaymentOrderCode())
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found for payment order code: " + event.getPaymentOrderCode()));

        if (event.isSuccess()) {

            // if checkout successfully, then set payment code for the subscription
            subscription.setPaymentOrderCode(event.getPaymentOrderCode());

            User subscriptedUser = subscription.getUser();
            subscriptedUser.setCurrentPackage("PREMIUM");

            // Payment successful
            subscriptionRepository.save(subscription);
            log.error("Payment succeed for order code: {}", event.getPaymentOrderCode());

        } else {

            // Payment failed - trigger compensation
            subscriptionRepository.delete(subscription);
            log.error("Payment failed for order code: {}", event.getPaymentOrderCode());
        }
    }

    private Subscription createSubscription(Long userId, Integer packageId) {
        UserSubscriptionId userSubscriptionId = new UserSubscriptionId();
        userSubscriptionId.setUserId(userId);
        userSubscriptionId.setPackageId(packageId);

        Subscription subscription = new Subscription();
        subscription.setUserSubscriptionId(userSubscriptionId);
        subscription.setCreatedAt(LocalDateTime.now());
        return subscription;
    }

}

    