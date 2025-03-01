package com.shopease.dto.response;

import com.shopease.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for returning order information in API responses.
 */
@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private String discountCode;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
    private PaymentResponse payment;

    /**
     * Creates an OrderResponse from an Order entity.
     *
     * @param order The order entity
     * @return An OrderResponse DTO
     */
    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setStatus(order.getStatus().name());
        response.setTotalPrice(order.getTotalPrice());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setFinalPrice(order.getFinalPrice());

        if (order.getAppliedDiscount() != null) {
            response.setDiscountCode(order.getAppliedDiscount().getCode());
        }

        response.setOrderDate(order.getOrderDate());

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList());

        response.setItems(itemResponses);

        if (order.getPaymentRecord() != null) {
            response.setPayment(PaymentResponse.fromEntity(order.getPaymentRecord()));
        }

        return response;
    }
}