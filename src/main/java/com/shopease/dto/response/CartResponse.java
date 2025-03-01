package com.shopease.dto.response;

import com.shopease.model.Cart;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for returning cart information in API responses.
 */
@Data
public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private int itemCount;
    private LocalDateTime lastAccessed;

    /**
     * Creates a CartResponse from a Cart entity.
     *
     * @param cart The cart entity
     * @return A CartResponse DTO
     */
    public static CartResponse fromEntity(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(CartItemResponse::fromEntity)
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        response.setTotalAmount(cart.calculateTotal());
        response.setItemCount(cart.getItems().size());
        response.setLastAccessed(cart.getLastAccessed());

        return response;
    }
}