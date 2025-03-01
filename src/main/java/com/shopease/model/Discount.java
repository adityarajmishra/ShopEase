package com.shopease.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Entity representing a discount code in the e-commerce system.
 * Includes validation and logic for calculating discount amounts.
 */
@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
public class Discount extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    @PositiveOrZero
    private BigDecimal percentage;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Integer maxUsage;

    @Column(nullable = false)
    private Integer currentUsage = 0;

    /**
     * Checks if the discount is currently valid based on date and usage count.
     *
     * @return true if discount is valid, false otherwise
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) &&
                now.isBefore(expiryDate) &&
                currentUsage < maxUsage;
    }

    /**
     * Increments the usage count of this discount.
     */
    public void incrementUsage() {
        this.currentUsage++;
    }

    /**
     * Calculates the discount amount for a given price.
     *
     * @param amount The amount to apply the discount to
     * @return The calculated discount amount
     */
    public BigDecimal calculateDiscount(BigDecimal amount) {
        return amount.multiply(percentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }
}