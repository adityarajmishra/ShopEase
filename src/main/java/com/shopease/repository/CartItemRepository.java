package com.shopease.repository;

import com.shopease.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItem entity.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Finds all items in a specific cart.
     *
     * @param cartId The cart ID to find items for
     * @return A list of items in the specified cart
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * Finds a specific item in a cart by product ID.
     *
     * @param cartId The cart ID to search in
     * @param productId The product ID to find
     * @return An Optional containing the cart item if found, or empty otherwise
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Deletes all items in a specific cart.
     *
     * @param cartId The cart ID to clear
     */
    void deleteByCartId(Long cartId);

    /**
     * Deletes a specific item from a cart.
     *
     * @param cartId The cart ID to modify
     * @param productId The product ID to remove
     */
    void deleteByCartIdAndProductId(Long cartId, Long productId);
}