package com.shopease.dto.response;

import com.shopease.model.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for returning cart item information in API responses.
 */
@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal total;
    private LocalDateTime addedAt;

    /**
     * Creates a CartItemResponse from a CartItem entity.
     *
     * @param cartItem The cart item entity
     * @return A CartItemResponse DTO
     */
    public static CartItemResponse fromEntity(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProduct().getId());
        response.setProductName(cartItem.getProduct().getName());
        response.setPrice(cartItem.getProduct().getPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setTotal(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        response.setAddedAt(cartItem.getAddedAt());
        return response;
    }
}