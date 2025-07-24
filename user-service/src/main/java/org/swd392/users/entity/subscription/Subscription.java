package org.swd392.users.entity.subscription;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.swd392.users.entity.Package;
import org.swd392.users.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subscription")
public class Subscription {

    @EmbeddedId
    private UserSubscriptionId userSubscriptionId;

    @Column(name = "payment_order_code")
    private String paymentOrderCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @MapsId("packageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private Package packageType;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
