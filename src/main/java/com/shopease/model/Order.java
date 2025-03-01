package com.shopease.model;

import com.shopease.exception.InvalidOrderStateException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing an order in the e-commerce system.
 * An order is created when a user checks out their cart.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> items = new HashSet<>();

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private BigDecimal finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount appliedDiscount;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentRecord paymentRecord;

    /**
     * Enum representing possible order statuses with state transition rules.
     */
    public enum OrderStatus {
        PENDING,
        COMPLETED,
        CANCELLED;

        /**
         * Checks if transition to a new status is allowed based on current status.
         *
         * @param newStatus The target status
         * @return true if transition is allowed, false otherwise
         */
        public boolean canTransitionTo(OrderStatus newStatus) {
            if (this == PENDING) {
                return newStatus == COMPLETED || newStatus == CANCELLED;
            } else if (this == COMPLETED) {
                return false; // Completed orders cannot change status
            } else if (this == CANCELLED) {
                return false; // Cancelled orders cannot change status
            }
            return false;
        }
    }

    /**
     * Checks if the order can be modified (only PENDING orders can be modified).
     *
     * @return true if order is modifiable, false otherwise
     */
    public boolean isModifiable() {
        return status == OrderStatus.PENDING;
    }

    /**
     * Applies a discount to the order and recalculates prices.
     *
     * @param discount The discount to apply
     * @throws InvalidOrderStateException if the order is not in PENDING state
     */
    public void applyDiscount(Discount discount) {
        if (!isModifiable()) {
            throw new InvalidOrderStateException("Cannot modify a non-pending order");
        }

        this.appliedDiscount = discount;
        recalculatePrices();
    }

    /**
     * Updates the order status following state transition rules.
     *
     * @param newStatus The new status to set
     * @throws InvalidOrderStateException if the transition is not allowed
     */
    public void updateStatus(OrderStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidOrderStateException(
                    "Cannot transition from " + status + " to " + newStatus);
        }
        this.status = newStatus;
    }

    /**
     * Recalculates all prices (total, discount, final) based on current items and discount.
     */
    private void recalculatePrices() {
        this.totalPrice = calculateTotalPrice();
        this.discountAmount = calculateDiscountAmount();
        this.finalPrice = totalPrice.subtract(discountAmount);
    }

    /**
     * Calculates the total price of all items in the order.
     *
     * @return The total price
     */
    private BigDecimal calculateTotalPrice() {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the discount amount based on the applied discount.
     *
     * @return The discount amount
     */
    private BigDecimal calculateDiscountAmount() {
        if (appliedDiscount == null) {
            return BigDecimal.ZERO;
        }

        return appliedDiscount.calculateDiscount(totalPrice);
    }

    /**
     * Factory method for creating an order from a cart.
     *
     * @param cart The cart to convert to an order
     * @param user The user placing the order
     * @return A new Order entity populated with cart items
     */
    public static Order createFromCart(Cart cart, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        // Convert cart items to order items
        cart.getItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            order.getItems().add(orderItem);
        });

        order.recalculatePrices();
        return order;
    }
}