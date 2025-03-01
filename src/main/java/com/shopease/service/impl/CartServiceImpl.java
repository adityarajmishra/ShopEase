package com.shopease.service.impl;

import com.shopease.dto.request.AddToCartRequest;
import com.shopease.dto.response.CartResponse;
import com.shopease.event.CartExpiredEvent;
import com.shopease.exception.InsufficientStockException;
import com.shopease.exception.InvalidCartOperationException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.Cart;
import com.shopease.model.CartItem;
import com.shopease.model.Product;
import com.shopease.model.User;
import com.shopease.repository.CartItemRepository;
import com.shopease.repository.CartRepository;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.UserRepository;
import com.shopease.service.CartService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CartService interface.
 */
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.cart.expiry.hours:24}")
    private long cartExpiryHours;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setLastAccessed(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional
    public CartItem addItemToCart(Long userId, AddToCartRequest request) {
        if (request.getQuantity() <= 0) {
            throw new InvalidCartOperationException("Quantity must be positive");
        }

        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // Check if product has enough stock
        if (!product.hasStock(request.getQuantity())) {
            throw new InsufficientStockException("Not enough stock available for product: " + product.getName());
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        // Add or update item
        if (existingItem.isPresent()) {
            // Update existing item
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            // Check if product has enough stock for the new total quantity
            if (!product.hasStock(newQuantity)) {
                throw new InsufficientStockException("Not enough stock available for product: " + product.getName());
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
            return item;
        } else {
            // Add new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.setAddedAt(LocalDateTime.now());

            cart.getItems().add(newItem);
            cart.updateLastAccessed();

            cartRepository.save(cart);
            return newItem;
        }
    }

    @Override
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long itemId, Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new InvalidCartOperationException("Quantity must be positive");
        }

        Cart cart = getOrCreateCart(userId);

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        // Check if product has enough stock
        if (!itemToUpdate.getProduct().hasStock(newQuantity)) {
            throw new InsufficientStockException("Not enough stock available for product: " + itemToUpdate.getProduct().getName());
        }

        // Update the quantity
        itemToUpdate.setQuantity(newQuantity);
        cart.updateLastAccessed();

        // Save and return updated cart
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItemFromCart(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);

        // Find the item to remove
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        // Remove the item
        cart.getItems().remove(itemToRemove);
        cart.updateLastAccessed();

        // Save and return updated cart
        cartItemRepository.delete(itemToRemove);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartResponse(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return CartResponse.fromEntity(cart);
    }

    @Override
    @Scheduled(fixedRate = 86400000) // Run once per day
    @Transactional
    public void cleanupExpiredCarts() {
        List<Cart> expiredCarts = cartRepository.findByLastAccessedBefore(
                LocalDateTime.now().minusHours(cartExpiryHours));

        for (Cart cart : expiredCarts) {
            eventPublisher.publishEvent(new CartExpiredEvent(cart));
            cartRepository.delete(cart);
        }
    }
}