package com.shopease.service;

import com.shopease.model.Order;
import com.shopease.model.PaymentRecord;

/**
 * Service interface for payment-related operations.
 */
public interface PaymentService {

    /**
     * Processes payment for an order.
     *
     * @param order The order to process payment for
     * @return true if payment is successful, false otherwise
     */
    boolean processPayment(Order order);

    /**
     * Gets a payment record by order ID.
     *
     * @param orderId The order ID
     * @return The payment record
     */
    PaymentRecord getPaymentByOrderId(Long orderId);
}