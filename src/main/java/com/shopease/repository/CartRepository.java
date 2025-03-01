package com.shopease.repository;

import com.shopease.model.Cart;
import com.shopease.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Cart entity.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds a cart by user.
     *
     * @param user The user to find the cart for
     * @return An Optional containing the cart if found, or empty otherwise
     */
    Optional<Cart> findByUser(User user);

    /**
     * Finds a cart by user ID.
     *
     * @param userId The user ID to find the cart for
     * @return An Optional containing the cart if found, or empty otherwise
     */
    Optional<Cart> findByUserId(Long userId);

    /**
     * Finds all carts that have not been accessed since the specified time.
     * Used for cleanup of old carts.
     *
     * @param lastAccessedBefore The cutoff time
     * @return A list of carts not accessed since the cutoff time
     */
    List<Cart> findByLastAccessedBefore(LocalDateTime lastAccessedBefore);

    /**
     * Counts the number of abandoned carts (not accessed in a specified period).
     *
     * @param lastAccessedBefore The cutoff time
     * @return The count of abandoned carts
     */
    long countByLastAccessedBefore(LocalDateTime lastAccessedBefore);

    /**
     * Finds carts with products from a specific category.
     *
     * @param category The category to search for
     * @return A list of carts containing products from the specified category
     */
    @Query("SELECT DISTINCT c FROM Cart c JOIN c.items i JOIN i.product p WHERE p.category = :category")
    List<Cart> findCartsWithProductCategory(@Param("category") String category);
}