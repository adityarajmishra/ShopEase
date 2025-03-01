package com.shopease.model;

import com.shopease.exception.InsufficientStockException;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity representing a product in the e-commerce system.
 * Includes product details and stock management.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    @PositiveOrZero
    private BigDecimal price;

    @Column(nullable = false)
    @PositiveOrZero
    private Integer stockQuantity;

    @Column(nullable = false)
    private String category;

    // Product status using State pattern
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    /**
     * Enum representing possible product statuses.
     */
    public enum ProductStatus {
        ACTIVE,
        OUT_OF_STOCK,
        DISCONTINUED
    }

    /**
     * Checks if the product has enough stock for a requested quantity.
     *
     * @param quantity The requested quantity
     * @return true if enough stock is available, false otherwise
     */
    public boolean hasStock(int quantity) {
        return this.stockQuantity >= quantity;
    }

    /**
     * Reduces the product stock by the specified quantity.
     * Updates product status to OUT_OF_STOCK if stock reaches zero.
     *
     * @param quantity The quantity to reduce
     * @throws InsufficientStockException if there's not enough stock
     */
    public void reduceStock(int quantity) {
        if (!hasStock(quantity)) {
            throw new InsufficientStockException("Not enough stock available for product: " + this.name);
        }
        this.stockQuantity -= quantity;
        updateStatus();
    }

    /**
     * Restores product stock by the specified quantity.
     * Updates product status to ACTIVE if stock was previously zero.
     *
     * @param quantity The quantity to add back to stock
     */
    public void restoreStock(int quantity) {
        this.stockQuantity += quantity;
        updateStatus();
    }

    /**
     * Updates the product status based on current stock level.
     */
    private void updateStatus() {
        if (this.stockQuantity == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else if (this.status == ProductStatus.OUT_OF_STOCK && this.stockQuantity > 0) {
            this.status = ProductStatus.ACTIVE;
        }
    }
}