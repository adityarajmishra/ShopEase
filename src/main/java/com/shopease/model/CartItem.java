package com.shopease.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing an item in a shopping cart.
 * Links a product with a cart and tracks quantity.
 */
@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Positive
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    /**
     * Increases the item quantity by the specified amount.
     *
     * @param additionalQuantity The quantity to add
     */
    public void incrementQuantity(int additionalQuantity) {
        this.quantity += additionalQuantity;
    }
}