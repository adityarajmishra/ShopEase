package com.shopease.service.impl;

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
import com.shopease.service.OrderService;
import com.shopease.service.PaymentService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the OrderService interface.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final DiscountRepository discountRepository;
    private final PaymentService paymentService;
    private final ApplicationEventPublisher eventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            DiscountRepository discountRepository,
                            PaymentService paymentService,
                            ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.discountRepository = discountRepository;
        this.paymentService = paymentService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Order createOrder(Long userId, String discountCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with id: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new InvalidOrderException("Cannot create order with empty cart");
        }

        // Verify stock for all items
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (!product.hasStock(item.getQuantity())) {
                throw new InvalidOrderException("Not enough stock for product: " + product.getName());
            }
        }

        // Create order from cart
        Order order = Order.createFromCart(cart, user);

        // Apply discount if provided
        if (StringUtils.hasText(discountCode)) {
            Discount discount = discountRepository.findByCode(discountCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Discount code not found: " + discountCode));

            if (!discount.isValid()) {
                throw new InvalidDiscountException("Discount code is expired or exceeded usage limit");
            }

            order.applyDiscount(discount);
            discount.incrementUsage();
            discountRepository.save(discount);
        }

        // Save order
        order = orderRepository.save(order);

        // Reduce stock for all products
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.reduceStock(item.getQuantity());
            productRepository.save(product);
        }

        // Clear cart
        cartRepository.delete(cart);

        // Publish order created event
        eventPublisher.publishEvent(new OrderCreatedEvent(order));

        return order;
    }

    @Override
    @Transactional
    public Order processPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Can only process payment for pending orders");
        }

        boolean paymentSuccessful = paymentService.processPayment(order);

        if (paymentSuccessful) {
            order.updateStatus(Order.OrderStatus.COMPLETED);
            order = orderRepository.save(order);
            eventPublisher.publishEvent(new OrderCompletedEvent(order));
        }

        return order;
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only pending orders can be cancelled");
        }

        // Restore stock for all products
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.restoreStock(item.getQuantity());
            productRepository.save(product);
        }

        order.updateStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCancelledEvent(order));

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getUserOrdersPaged(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);

        return new PagedResponse<>(
                orders.getContent().stream().map(OrderResponse::fromEntity).collect(Collectors.toList()),
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId + " for user with id: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by("orderDate").descending());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrdersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orders = orderRepository.findAll(pageable);

        return new PagedResponse<>(
                orders.getContent().stream().map(OrderResponse::fromEntity).collect(Collectors.toList()),
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getOrdersByStatus(Order.OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orders = orderRepository.findByStatus(status, pageable);

        return new PagedResponse<>(
                orders.getContent().stream().map(OrderResponse::fromEntity).collect(Collectors.toList()),
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateRevenueForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        Double revenue = orderRepository.calculateRevenueForPeriod(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }
}