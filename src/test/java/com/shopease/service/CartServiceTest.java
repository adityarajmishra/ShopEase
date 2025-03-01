package com.shopease.service;

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
import com.shopease.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private Long userId = 1L;
    private Long productId = 1L;
    private Long cartItemId = 1L;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(10);
        product.setCategory("Electronics");
        product.setStatus(Product.ProductStatus.ACTIVE);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setLastAccessed(LocalDateTime.now());

        cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setAddedAt(LocalDateTime.now());

        cart.setItems(new HashSet<>(Collections.singletonList(cartItem)));
    }

    @Test
    public void testGetOrCreateCart_ExistingCart() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // Act
        Cart result = cartService.getOrCreateCart(userId);

        // Assert
        assertNotNull(result);
        assertEquals(cart, result);

        verify(userRepository).findById(userId);
        verify(cartRepository).findByUser(user);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testGetOrCreateCart_CreateNewCart() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setId(1L);
            return savedCart;
        });

        // Act
        Cart result = cartService.getOrCreateCart(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(user, result.getUser());
        assertNotNull(result.getLastAccessed());

        verify(userRepository).findById(userId);
        verify(cartRepository).findByUser(user);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    public void testGetOrCreateCart_UserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.getOrCreateCart(userId);
        });

        verify(userRepository).findById(userId);
        verify(cartRepository, never()).findByUser(any(User.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddItemToCart_NewItem() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(3);

        when(cartService.getOrCreateCart(userId)).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Create a cart without the item we're adding
        cart.setItems(new HashSet<>());

        // Act
        CartItem result = cartService.addItemToCart(userId, request);

        // Assert
        assertNotNull(result);
        assertEquals(product, result.getProduct());
        assertEquals(request.getQuantity(), result.getQuantity());

        verify(productRepository).findById(productId);
        verify(cartRepository).save(cart);
    }

    @Test
    public void testAddItemToCart_ExistingItem() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(3);

        CartItem existingCartItem = new CartItem();
        existingCartItem.setId(cartItemId);
        existingCartItem.setCart(cart);
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(2);
        existingCartItem.setAddedAt(LocalDateTime.now());

        cart.getItems().clear();
        cart.getItems().add(existingCartItem);

        when(cartService.getOrCreateCart(userId)).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(existingCartItem);

        // Act
        CartItem result = cartService.addItemToCart(userId, request);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getQuantity()); // 2 (existing) + 3 (added)

        verify(productRepository).findById(productId);
        verify(cartItemRepository).save(existingCartItem);
    }

    @Test
    public void testAddItemToCart_InsufficientStock() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(20); // More than available stock (10)

        when(cartService.getOrCreateCart(userId)).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> {
            cartService.addItemToCart(userId, request);
        });

        verify(productRepository).findById(productId);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddItemToCart_ZeroQuantity() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(0);

        // Act & Assert
        assertThrows(InvalidCartOperationException.class, () -> {
            cartService.addItemToCart(userId, request);
        });

        verify(productRepository, never()).findById(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    public void testUpdateCartItemQuantity_Success() {
        // Arrange
        when(cartService.getOrCreateCart(userId)).thenReturn(cart);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.updateCartItemQuantity(userId, cartItemId, 5);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());

        CartItem updatedItem = result.getItems().iterator().next();
        assertEquals(5, updatedItem.getQuantity());

        verify(cartRepository).save(cart);
    }

    @Test
    public void testUpdateCartItemQuantity_ItemNotFound() {
        // Arrange
        Long nonExistentItemId = 999L;
        when(cartService.getOrCreateCart(userId)).thenReturn(cart);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.updateCartItemQuantity(userId, nonExistentItemId, 5);
        });

        verify(cartRepository, never()).save(any());
    }

    @Test
    public void testRemoveItemFromCart_Success() {
        // Arrange
        when(cartService.getOrCreateCart(userId)).thenReturn(cart);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        doNothing().when(cartItemRepository).delete(any(CartItem.class));

        // Act
        Cart result = cartService.removeItemFromCart(userId, cartItemId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());

        verify(cartItemRepository).delete(any(CartItem.class));
        verify(cartRepository).save(cart);
    }

    @Test
    public void testRemoveItemFromCart_ItemNotFound() {
        // Arrange
        Long nonExistentItemId = 999L;
        when(cartService.getOrCreateCart(userId)).thenReturn(cart);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.removeItemFromCart(userId, nonExistentItemId);
        });

        verify(cartItemRepository, never()).delete(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    public void testGetCartResponse_Success() {
        // Arrange
        when(cartService.getOrCreateCart(userId)).thenReturn(cart);

        // Act
        CartResponse result = cartService.getCartResponse(userId);

        // Assert
        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        assertEquals(cart.getUser().getId(), result.getUserId());
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("199.98"), result.getTotalAmount()); // 99.99 * 2
    }

    @Test
    public void testCleanupExpiredCarts() {
        // Arrange
        List<Cart> expiredCarts = Arrays.asList(cart);
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

        when(cartRepository.findByLastAccessedBefore(any(LocalDateTime.class))).thenReturn(expiredCarts);
        doNothing().when(eventPublisher).publishEvent(any(CartExpiredEvent.class));
        doNothing().when(cartRepository).delete(any(Cart.class));

        // Act
        cartService.cleanupExpiredCarts();

        // Assert
        verify(cartRepository).findByLastAccessedBefore(any(LocalDateTime.class));
        verify(eventPublisher).publishEvent(any(CartExpiredEvent.class));
        verify(cartRepository).delete(cart);
    }
}