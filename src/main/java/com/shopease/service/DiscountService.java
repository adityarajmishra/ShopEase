package com.shopease.service;

import com.shopease.dto.request.DiscountRequest;
import com.shopease.dto.response.DiscountResponse;
import com.shopease.dto.response.PagedResponse;
import com.shopease.model.Discount;

import java.util.List;

/**
 * Service interface for discount-related operations.
 */
public interface DiscountService {

    /**
     * Creates a new discount.
     *
     * @param discountRequest The discount data
     * @return The created discount
     */
    Discount createDiscount(DiscountRequest discountRequest);

    /**
     * Updates an existing discount.
     *
     * @param id The discount ID
     * @param discountRequest The updated discount data
     * @return The updated discount
     */
    Discount updateDiscount(Long id, DiscountRequest discountRequest);

    /**
     * Validates a discount code.
     *
     * @param code The discount code to validate
     * @return The discount if valid
     */
    Discount validateDiscountCode(String code);

    /**
     * Gets all discounts.
     *
     * @return A list of all discounts
     */
    List<Discount> getAllDiscounts();

    /**
     * Gets all discounts with pagination.
     *
     * @param page The page number
     * @param size The page size
     * @return A paged response of discounts
     */
    PagedResponse<DiscountResponse> getAllDiscountsPaged(int page, int size);

    /**
     * Gets all active discounts.
     *
     * @return A list of active discounts
     */
    List<Discount> getActiveDiscounts();
}