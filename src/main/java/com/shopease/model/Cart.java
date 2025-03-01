package com.shopease.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Entity representing a shopping cart in the e-commerce system.
 * A cart is associated with a user and contains cart items.
 */
@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
public class Cart extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> items = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime lastAccessed;

    /**
     * Adds a product to the cart with the specified quantity.
     * If the product already exists in the cart, the quantity is incremented.
     *
     * @param product The product to add
     * @param quantity The quantity to add
     */
    public void addItem(Product product, int quantity) {
        Optional<CartItem> existingItem = findItemByProduct(product);

        if (existingItem.isPresent()) {
            existingItem.get().incrementQuantity(quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(this);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setAddedAt(LocalDateTime.now());
            items.add(newItem);
        }
        updateLastAccessed();
    }

    /**
     * Updates the quantity of a cart item.
     *
     * @param productId The ID of the product to update
     * @param newQuantity The new quantity
     */
    public void updateItemQuantity(Long productId, int newQuantity) {
        findItemByProductId(productId).ifPresent(item -> {
            item.setQuantity(newQuantity);
            updateLastAccessed();
        });
    }

    /**
     * Removes an item from the cart.
     *
     * @param productId The ID of the product to remove
     */
    public void removeItem(Long productId) {
        findItemByProductId(productId).ifPresent(item -> {
            items.remove(item);
            updateLastAccessed();
        });
    }

    /**
     * Calculates the total value of all items in the cart.
     *
     * @return The total cart value
     */
    public BigDecimal calculateTotal() {
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Finds a cart item by product.
     *
     * @param product The product to find
     * @return An Optional containing the cart item if found, or empty otherwise
     */
    private Optional<CartItem> findItemByProduct(Product product) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();
    }

    /**
     * Finds a cart item by product ID.
     *
     * @param productId The product ID to find
     * @return An Optional containing the cart item if found, or empty otherwise
     */
    private Optional<CartItem> findItemByProductId(Long productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    /**
     * Updates the last accessed timestamp to the current time.
     */
    public void updateLastAccessed() {
        this.lastAccessed = LocalDateTime.now();
    }

    /**
     * Checks if the cart has expired based on the provided expiry time.
     *
     * @param expiryTimeInHours The cart expiry time in hours
     * @return true if the cart has expired, false otherwise
     */
    public boolean isExpired(long expiryTimeInHours) {
        return lastAccessed.plusHours(expiryTimeInHours).isBefore(LocalDateTime.now());
    }
}