package com.shopease.controller;

import com.shopease.dto.request.CheckoutRequest;
import com.shopease.dto.response.OrderResponse;
import com.shopease.dto.response.PagedResponse;
import com.shopease.model.Order;
import com.shopease.security.CurrentUser;
import com.shopease.security.UserPrincipal;
import com.shopease.service.OrderService;
import com.shopease.util.AppConstants;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for order operations.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order from the current user's cart.
     *
     * @param currentUser The authenticated user
     * @param request The checkout request
     * @return ResponseEntity with the created order
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponse> createOrder(
            @CurrentUser UserPrincipal currentUser,
            @RequestBody(required = false) CheckoutRequest request) {

        String discountCode = request != null ? request.getDiscountCode() : null;
        Order order = orderService.createOrder(currentUser.getId(), discountCode);

        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromEntity(order));
    }

    /**
     * Gets all orders for the current user.
     *
     * @param currentUser The authenticated user
     * @param page The page number
     * @param size The page size
     * @return ResponseEntity with paged orders
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PagedResponse<OrderResponse>> getUserOrders(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        PagedResponse<OrderResponse> response = orderService.getUserOrdersPaged(currentUser.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a specific order for the current user.
     *
     * @param currentUser The authenticated user
     * @param orderId The order ID
     * @return ResponseEntity with the order
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponse> getOrderById(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long orderId) {

        Order order = orderService.getOrderById(currentUser.getId(), orderId);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    /**
     * Processes payment for an order.
     *
     * @param currentUser The authenticated user
     * @param orderId The order ID
     * @return ResponseEntity with the updated order
     */
    @PostMapping("/{orderId}/payment")
    @PreAuthorize("hasRole('USER') and @orderSecurity.isOrderOwner(authentication, #orderId)")
    public ResponseEntity<OrderResponse> processPayment(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long orderId) {

        Order order = orderService.processPayment(orderId);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    /**
     * Cancels an order.
     *
     * @param currentUser The authenticated user
     * @param orderId The order ID
     * @return ResponseEntity with the updated order
     */
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER') and @orderSecurity.isOrderOwner(authentication, #orderId)")
    public ResponseEntity<OrderResponse> cancelOrder(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long orderId) {

        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    /**
     * Gets all orders (admin only).
     *
     * @param page The page number
     * @param size The page size
     * @return ResponseEntity with paged orders
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        PagedResponse<OrderResponse> response = orderService.getAllOrdersPaged(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets orders by status (admin only).
     *
     * @param status The order status
     * @param page The page number
     * @param size The page size
     * @return ResponseEntity with paged orders
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<OrderResponse>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        PagedResponse<OrderResponse> response = orderService.getOrdersByStatus(status, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets orders by date range (admin only).
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return ResponseEntity with orders
     */
    @GetMapping("/admin/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Gets revenue for a specific period (admin only).
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return ResponseEntity with revenue
     */
    @GetMapping("/admin/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getRevenueForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Double revenue = orderService.calculateRevenueForPeriod(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
}