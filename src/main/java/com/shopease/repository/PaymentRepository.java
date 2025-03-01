package com.shopease.repository;

import com.shopease.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PaymentRecord entity.
 */
@Repository
public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {

    /**
     * Finds a payment record by order ID.
     *
     * @param orderId The order ID to find the payment for
     * @return An Optional containing the payment record if found, or empty otherwise
     */
    Optional<PaymentRecord> findByOrderId(Long orderId);

    /**
     * Finds a payment record by transaction reference.
     *
     * @param transactionReference The transaction reference to search for
     * @return An Optional containing the payment record if found, or empty otherwise
     */
    Optional<PaymentRecord> findByTransactionReference(String transactionReference);
}