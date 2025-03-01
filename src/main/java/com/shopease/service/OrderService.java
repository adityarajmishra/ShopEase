package com.shopease.service;

import com.shopease.dto.response.OrderResponse;
import com.shopease.dto.response.PagedResponse;
import com.shopease.model.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for order-related operations.
 */
public interface OrderService {

    /**
     * Creates a new order from a user's cart.
     *
     * @param userId The user ID
     * @param discountCode The discount code to apply (optional)
     * @return The created order
     */
    Order createOrder(Long userId, String discountCode);

    /**
     * Processes payment for an order.
     *
     * @param orderId The order ID
     * @return The updated order
     */
    Order processPayment(Long orderId);

    /**
     * Cancels an order.
     *
     * @param orderId The order ID
     * @return The updated order
     */
    Order cancelOrder(Long orderId);

    /**
     * Gets all orders for a user.
     *
     * @param userId The user ID
     * @return A list of orders
     */
    List<Order> getUserOrders(Long userId);

    /**
     * Gets all orders for a user with pagination.
     *
     * @param userId The user ID
     * @param page The page number
     * @param size The page size
     * @return A paged response of orders
     */
    PagedResponse<OrderResponse> getUserOrdersPaged(Long userId, int page, int size);

    /**
     * Gets a specific order for a user.
     *
     * @param userId The user ID
     * @param orderId The order ID
     * @return The order
     */
    Order getOrderById(Long userId, Long orderId);

    /**
     * Gets all orders (admin only).
     *
     * @return A list of all orders
     */
    List<Order> getAllOrders();

    /**
     * Gets all orders with pagination (admin only).
     *
     * @param page The page number
     * @param size The page size
     * @return A paged response of orders
     */
    PagedResponse<OrderResponse> getAllOrdersPaged(int page, int size);

    /**
     * Gets orders by status with pagination (admin only).
     *
     * @param status The order status
     * @param page The page number
     * @param size The page size
     * @return A paged response of orders
     */
    PagedResponse<OrderResponse> getOrdersByStatus(Order.OrderStatus status, int page, int size);

    /**
     * Gets orders created within a specific date range (admin only).
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of orders
     */
    List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calculates the total revenue for a specific period (admin only).
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return The total revenue
     */
    Double calculateRevenueForPeriod(LocalDateTime startDate, LocalDateTime endDate);
}