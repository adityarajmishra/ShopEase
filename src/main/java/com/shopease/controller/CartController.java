package com.shopease.controller;

import com.shopease.dto.request.AddToCartRequest;
import com.shopease.dto.request.UpdateCartItemRequest;
import com.shopease.dto.response.CartResponse;
import com.shopease.model.Cart;
import com.shopease.model.CartItem;
import com.shopease.security.CurrentUser;
import com.shopease.security.UserPrincipal;
import com.shopease.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for cart operations.
 */
@RestController
@RequestMapping("/cart")
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Gets the current user's cart.
     *
     * @param currentUser The authenticated user
     * @return ResponseEntity with the cart
     */
    @GetMapping
    public ResponseEntity<CartResponse> getUserCart(@CurrentUser UserPrincipal currentUser) {
        CartResponse cartResponse = cartService.getCartResponse(currentUser.getId());
        return ResponseEntity.ok(cartResponse);
    }

    /**
     * Adds an item to the cart.
     *
     * @param currentUser The authenticated user
     * @param request The add to cart request
     * @return ResponseEntity with the updated cart
     */
    @PostMapping
    public ResponseEntity<CartResponse> addItemToCart(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody AddToCartRequest request) {

        cartService.addItemToCart(currentUser.getId(), request);
        CartResponse cartResponse = cartService.getCartResponse(currentUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    /**
     * Updates the quantity of a cart item.
     *
     * @param currentUser The authenticated user
     * @param itemId The cart item ID
     * @param request The update cart item request
     * @return ResponseEntity with the updated cart
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        cartService.updateCartItemQuantity(currentUser.getId(), itemId, request.getQuantity());
        CartResponse cartResponse = cartService.getCartResponse(currentUser.getId());

        return ResponseEntity.ok(cartResponse);
    }

    /**
     * Removes an item from the cart.
     *
     * @param currentUser The authenticated user
     * @param itemId The cart item ID
     * @return ResponseEntity with the updated cart
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long itemId) {

        cartService.removeItemFromCart(currentUser.getId(), itemId);
        CartResponse cartResponse = cartService.getCartResponse(currentUser.getId());

        return ResponseEntity.ok(cartResponse);
    }

    /**
     * Clears the cart.
     *
     * @param currentUser The authenticated user
     * @return ResponseEntity with the empty cart
     */
    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart(@CurrentUser UserPrincipal currentUser) {
        Cart cart = cartService.getOrCreateCart(currentUser.getId());

        // Clear all items
        cart.getItems().clear();
        cart.updateLastAccessed();

        CartResponse cartResponse = cartService.getCartResponse(currentUser.getId());
        return ResponseEntity.ok(cartResponse);
    }
}