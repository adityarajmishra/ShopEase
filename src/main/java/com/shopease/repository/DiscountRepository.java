package com.shopease.repository;

import com.shopease.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Discount entity.
 */
@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    /**
     * Finds a discount by code.
     *
     * @param code The code to search for
     * @return An Optional containing the discount if found, or empty otherwise
     */
    Optional<Discount> findByCode(String code);

    /**
     * Checks if a discount with the given code exists.
     *
     * @param code The code to check
     * @return true if a discount with the code exists, false otherwise
     */
    boolean existsByCode(String code);

    /**
     * Finds all active discounts (current date between start and expiry, and not fully used).
     *
     * @param currentDate The current date
     * @return A list of active discounts
     */
    List<Discount> findByStartDateBeforeAndExpiryDateAfterAndCurrentUsageLessThanMaxUsage(
            LocalDateTime currentDate, LocalDateTime currentDate2, Integer maxUsage);

    /**
     * Finds all expired discounts.
     *
     * @param currentDate The current date
     * @return A list of expired discounts
     */
    List<Discount> findByExpiryDateBefore(LocalDateTime currentDate);
}