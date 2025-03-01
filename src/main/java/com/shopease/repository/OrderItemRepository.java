package com.shopease.repository;

import com.shopease.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderItem entity.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Finds all items in a specific order.
     *
     * @param orderId The order ID to find items for
     * @return A list of items in the specified order
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Finds the most popular products based on sales quantity.
     *
     * @param limit The maximum number of products to return
     * @return A list of order items for the most popular products
     */
    @Query(value = "SELECT oi.product_id, SUM(oi.quantity) as total_quantity FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status = 'COMPLETED' " +
            "GROUP BY oi.product_id " +
            "ORDER BY total_quantity DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findMostPopularProducts(@Param("limit") int limit);

    /**
     * Calculates the total sales for a specific product.
     *
     * @param productId The product ID to calculate sales for
     * @return The total quantity sold of the specified product
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi JOIN oi.order o WHERE oi.product.id = :productId AND o.status = 'COMPLETED'")
    Integer calculateTotalSalesForProduct(@Param("productId") Long productId);
}