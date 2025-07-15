package org.swd392.paymentservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swd392.paymentservice.entity.PaymentMetadata;

import java.util.Optional;

public interface PaymentMetadataRepository extends JpaRepository<PaymentMetadata, Long> {
    Optional<PaymentMetadata> findPaymentMetadataByOrderCode(Long orderCode);
}
