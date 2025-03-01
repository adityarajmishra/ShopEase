package com.shopease.service;

import com.shopease.dto.request.AddToCartRequest;
import com.shopease.dto.response.CartResponse;
import com.shopease.model.Cart;
import com.shopease.model.CartItem;

/**
 * Service interface for cart-related operations.
 */
public interface CartService {

    /**
     * Gets the cart for a user, creating one if it doesn't exist.
     *
     * @param userId The user ID
     * @return The cart
     */
    Cart getOrCreateCart(Long userId);

    /**
     * Adds an item to a cart.
     *
     * @param userId The user ID
     * @param request The add to cart request
     * @return The added cart item
     */
    CartItem addItemToCart(Long userId, AddToCartRequest request);

    /**
     * Updates the quantity of a cart item.
     *
     * @param userId The user ID
     * @param itemId The cart item ID
     * @param newQuantity The new quantity
     * @return The updated cart
     */
    Cart updateCartItemQuantity(Long userId, Long itemId, Integer newQuantity);

    /**
     * Removes an item from a cart.
     *
     * @param userId The user ID
     * @param itemId The cart item ID
     * @return The updated cart
     */
    Cart removeItemFromCart(Long userId, Long itemId);

    /**
     * Gets the cart for a user, formatted as a response DTO.
     *
     * @param userId The user ID
     * @return The cart response
     */
    CartResponse getCartResponse(Long userId);

    /**
     * Cleans up expired carts.
     */
    void cleanupExpiredCarts();
}