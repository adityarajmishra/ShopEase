package com.shopease.repository;

import com.shopease.model.Order;
import com.shopease.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders for a specific user ordered by date.
     *
     * @param user The user to find orders for
     * @return A list of orders for the specified user
     */
    List<Order> findByUserOrderByOrderDateDesc(User user);

    /**
     * Finds all orders for a specific user with pagination.
     *
     * @param userId The user ID to find orders for
     * @param pageable Pagination information
     * @return A page of orders for the specified user
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * Finds a specific order for a specific user.
     *
     * @param orderId The order ID to find
     * @param userId The user ID to check
     * @return An Optional containing the order if found, or empty otherwise
     */
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    /**
     * Finds orders by status.
     *
     * @param status The status to filter by
     * @return A list of orders with the specified status
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Finds orders by status with pagination.
     *
     * @param status The status to filter by
     * @param pageable Pagination information
     * @return A page of orders with the specified status
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    /**
     * Finds orders created within a specific date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of orders created within the specified date range
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calculates the total revenue for a specific period.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return The total revenue for the specified period
     */
    @Query("SELECT SUM(o.finalPrice) FROM Order o WHERE o.status = 'COMPLETED' AND o.orderDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueForPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}