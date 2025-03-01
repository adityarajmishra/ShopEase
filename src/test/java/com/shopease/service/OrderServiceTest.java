package com.shopease.service;

import com.shopease.dto.response.OrderResponse;
import com.shopease.dto.response.PagedResponse;
import com.shopease.event.OrderCancelledEvent;
import com.shopease.event.OrderCompletedEvent;
import com.shopease.event.OrderCreatedEvent;
import com.shopease.exception.InvalidDiscountException;
import com.shopease.exception.InvalidOrderException;
import com.shopease.exception.InvalidOrderStateException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.*;
import com.shopease.repository.*;
import com.shopease.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private Order order;
    private OrderItem orderItem;
    private Discount discount;
    private Long userId = 1L;
    private Long productId = 1L;
    private Long orderId = 1L;

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
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setAddedAt(LocalDateTime.now());

        cart.setItems(new HashSet<>(Collections.singletonList(cartItem)));

        order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setTotalPrice(new BigDecimal("199.98")); // 99.99 * 2
        order.setFinalPrice(new BigDecimal("199.98"));
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("99.99"));

        order.setItems(new HashSet<>(Collections.singletonList(orderItem)));

        discount = new Discount();
        discount.setId(1L);
        discount.setCode("TEST10");
        discount.setPercentage(new BigDecimal("10"));
        discount.setStartDate(LocalDateTime.now().minusDays(1));
        discount.setExpiryDate(LocalDateTime.now().plusDays(10));
        discount.setMaxUsage(100);
        discount.setCurrentUsage(0);
    }

    @Test
    public void testCreateOrder_Success_NoDiscount() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
        doNothing().when(cartRepository).delete(any(Cart.class));

        // Act
        Order result = orderService.createOrder(userId, null);

        // Assert
        assertNotNull(result);
        assertEquals(order, result);

        verify(userRepository).findById(userId);
        verify(cartRepository).findByUser(user);
        verify(orderRepository).save(any(Order.class));
        verify(productRepository).save(product);
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
        verify(cartRepository).delete(cart);
        verify(discountRepository, never()).findByCode(anyString());
    }

    @Test
    public void testCreateOrder_Success_WithDiscount() {
        // Arrange
        String discountCode = "TEST10";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(discountRepository.findByCode(discountCode)).thenReturn(Optional.of(discount));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(discountRepository.save(any(Discount.class))).thenReturn(discount);
        doNothing().when(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
        doNothing().when(cartRepository).delete(any(Cart.class));

        // Act
        Order result = orderService.createOrder(userId, discountCode);

        // Assert
        assertNotNull(result);
        assertEquals(order, result);

        verify(userRepository).findById(userId);
        verify(cartRepository).findByUser(user);
        verify(discountRepository).findByCode(discountCode);
        verify(discountRepository).save(discount);
        verify(orderRepository).save(any(Order.class));
        verify(productRepository).save(product);
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
        verify(cartRepository).delete(cart);
    }

    @Test
    public void testCreateOrder_EmptyCart() {
        // Arrange
        cart.getItems().clear();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(InvalidOrderException.class, () -> {
            orderService.createOrder(userId, null);
        });

        verify(userRepository).findById(userId);
        verify(cartRepository).findByUser(user);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrder_InsufficientStock() {
        // Arrange
        product.setStockQuantity(1); // Less than cart item quantity (2)

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(InvalidOrderException.class, () -> {
            orderService.createOrder(userId, null);
        });

        verify(userRepository).findById(userId);
        verify(cartRepository).findByUser(user);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrder_InvalidDiscount() {
        // Arrange
        String discountCode = "TEST10";
        discount.setExpiryDate(LocalDateTime.now().minusDays(1)); // Expired discount

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(discountRepository.findByCode(discountCode)).thenReturn(Optional.of(discount));

        // Act & Assert
        assertThrows(InvalidDiscountException.class, () -> {
            orderService.createOrder(userId, discountCode);
        });

        verify(userRepository).findById(userId);
        verify(cartRepository).findByUser(user);
        verify(discountRepository).findByCode(discountCode);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testProcessPayment_Success() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentService.processPayment(order)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(eventPublisher).publishEvent(any(OrderCompletedEvent.class));

        // Act
        Order result = orderService.processPayment(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(Order.OrderStatus.COMPLETED, result.getStatus());

        verify(orderRepository).findById(orderId);
        verify(paymentService).processPayment(order);
        verify(orderRepository).save(order);
        verify(eventPublisher).publishEvent(any(OrderCompletedEvent.class));
    }

    @Test
    public void testProcessPayment_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.processPayment(orderId);
        });

        verify(orderRepository).findById(orderId);
        verify(paymentService, never()).processPayment(any(Order.class));
    }

    @Test
    public void testProcessPayment_NotPendingOrder() {
        // Arrange
        order.setStatus(Order.OrderStatus.COMPLETED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidOrderStateException.class, () -> {
            orderService.processPayment(orderId);
        });

        verify(orderRepository).findById(orderId);
        verify(paymentService, never()).processPayment(any(Order.class));
    }

    @Test
    public void testCancelOrder_Success() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(eventPublisher).publishEvent(any(OrderCancelledEvent.class));

        // Act
        Order result = orderService.cancelOrder(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(Order.OrderStatus.CANCELLED, result.getStatus());

        verify(orderRepository).findById(orderId);
        verify(productRepository).save(product);
        verify(orderRepository).save(order);
        verify(eventPublisher).publishEvent(any(OrderCancelledEvent.class));
    }

    @Test
    public void testCancelOrder_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.cancelOrder(orderId);
        });

        verify(orderRepository).findById(orderId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testCancelOrder_NotPendingOrder() {
        // Arrange
        order.setStatus(Order.OrderStatus.COMPLETED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidOrderStateException.class, () -> {
            orderService.cancelOrder(orderId);
        });

        verify(orderRepository).findById(orderId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testGetUserOrders_Success() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findByUserOrderByOrderDateDesc(user)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getUserOrders(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order, result.get(0));

        verify(userRepository).findById(userId);
        verify(orderRepository).findByUserOrderByOrderDateDesc(user);
    }

    @Test
    public void testGetUserOrdersPaged_Success() {
        // Arrange
        int page = 0;
        int size = 10;
        List<Order> orders = Collections.singletonList(order);
        Page<Order> orderPage = new PageImpl<>(orders);

        when(orderRepository.findByUserId(userId, any(Pageable.class))).thenReturn(orderPage);

        // Act
        PagedResponse<OrderResponse> result = orderService.getUserOrdersPaged(userId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals(orders.size(), result.getTotalElements());

        OrderResponse orderResponse = result.getContent().get(0);
        assertEquals(order.getId(), orderResponse.getId());

        verify(orderRepository).findByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    public void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(order));

        // Act
        Order result = orderService.getOrderById(userId, orderId);

        // Assert
        assertNotNull(result);
        assertEquals(order, result);

        verify(orderRepository).findByIdAndUserId(orderId, userId);
    }

    @Test
    public void testGetOrderById_OrderNotFound() {
        // Arrange
        when(orderRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getOrderById(userId, orderId);
        });

        verify(orderRepository).findByIdAndUserId(orderId, userId);
    }
}