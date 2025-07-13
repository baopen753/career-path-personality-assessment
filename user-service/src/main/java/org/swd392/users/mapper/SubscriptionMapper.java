package org.swd392.users.mapper;

import org.swd392.users.dto.SubscriptionResponseDto;
import org.swd392.users.entity.subscription.Subscription;

public class SubscriptionMapper {

    public static SubscriptionResponseDto mapToSubscription(Subscription subscription) {

        if (subscription == null) {
            return null;
        }
        SubscriptionResponseDto responseDto = new SubscriptionResponseDto();
        responseDto.setCreatedAt(subscription.getCreatedAt());
        responseDto.setUserId(responseDto.getUserId());
        responseDto.setPackageType(subscription.getPackageType().getPackageName());
        return responseDto;

    }

}
