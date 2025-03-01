package com.shopease.dto.response;

import com.shopease.model.OrderItem;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for returning order item information in API responses.
 */
@Data
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal total;

    /**
     * Creates an OrderItemResponse from an OrderItem entity.
     *
     * @param orderItem The order item entity
     * @return An OrderItemResponse DTO
     */
    public static OrderItemResponse fromEntity(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setProductId(orderItem.getProduct().getId());
        response.setProductName(orderItem.getProduct().getName());
        response.setQuantity(orderItem.getQuantity());
        response.setPrice(orderItem.getPrice());
        response.setTotal(orderItem.getTotalPrice());
        return response;
    }
}