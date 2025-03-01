package com.shopease.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a payment record in the e-commerce system.
 * Used for simulating payments and tracking payment status.
 */
@Entity
@Table(name = "payment_records")
@Getter
@Setter
@NoArgsConstructor
public class PaymentRecord extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column
    private String transactionReference;

    /**
     * Enum representing possible payment statuses.
     */
    public enum PaymentStatus {
        PENDING,
        SUCCESSFUL,
        FAILED
    }

    /**
     * Factory method for creating a payment record from an order.
     *
     * @param order The order to create a payment for
     * @return A new PaymentRecord entity in PENDING status
     */
    public static PaymentRecord createPaymentRecord(Order order) {
        PaymentRecord payment = new PaymentRecord();
        payment.setOrder(order);
        payment.setAmount(order.getFinalPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        return payment;
    }

    /**
     * Simulates payment processing with a configurable success rate.
     * Updates payment status and generates a transaction reference on success.
     */
    public void processPayment() {
        // In a real system, this would integrate with a payment gateway
        boolean isSuccessful = simulatePaymentProcessing();

        if (isSuccessful) {
            this.status = PaymentStatus.SUCCESSFUL;
            this.transactionReference = "TX-" + UUID.randomUUID().toString().substring(0, 8);
        } else {
            this.status = PaymentStatus.FAILED;
        }
    }

    /**
     * Simulates payment processing with a 90% success rate.
     *
     * @return true if payment is successful, false otherwise
     */
    private boolean simulatePaymentProcessing() {
        // Simulate 90% success rate for payments
        return Math.random() < 0.9;
    }
}