package com.shopease.service.impl;

import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.Order;
import com.shopease.model.PaymentRecord;
import com.shopease.repository.PaymentRepository;
import com.shopease.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the PaymentService interface.
 * Simulates payment processing.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public boolean processPayment(Order order) {
        // Check if payment record already exists
        PaymentRecord paymentRecord = paymentRepository.findByOrderId(order.getId())
                .orElseGet(() -> PaymentRecord.createPaymentRecord(order));

        // Process payment
        paymentRecord.processPayment();

        // Save payment record
        paymentRepository.save(paymentRecord);

        // Return payment status
        return paymentRecord.getStatus() == PaymentRecord.PaymentStatus.SUCCESSFUL;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentRecord getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for order with id: " + orderId));
    }
}