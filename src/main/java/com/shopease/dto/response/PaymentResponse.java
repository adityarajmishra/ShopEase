package com.shopease.dto.response;

import com.shopease.model.PaymentRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for returning payment information in API responses.
 */
@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paymentDate;
    private String transactionReference;

    /**
     * Creates a PaymentResponse from a PaymentRecord entity.
     *
     * @param payment The payment record entity
     * @return A PaymentResponse DTO
     */
    public static PaymentResponse fromEntity(PaymentRecord payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrder().getId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus().name());
        response.setPaymentDate(payment.getPaymentDate());
        response.setTransactionReference(payment.getTransactionReference());
        return response;
    }
}