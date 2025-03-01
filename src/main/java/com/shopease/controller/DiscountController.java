package com.shopease.controller;

import com.shopease.dto.request.DiscountRequest;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.DiscountResponse;
import com.shopease.dto.response.PagedResponse;
import com.shopease.model.Discount;
import com.shopease.service.DiscountService;
import com.shopease.util.AppConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for discount operations.
 */
@RestController
@RequestMapping("/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    /**
     * Creates a new discount (admin only).
     *
     * @param discountRequest The discount data
     * @return ResponseEntity with the created discount
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountResponse> createDiscount(@Valid @RequestBody DiscountRequest discountRequest) {
        Discount discount = discountService.createDiscount(discountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(DiscountResponse.fromEntity(discount));
    }

    /**
     * Updates a discount (admin only).
     *
     * @param id The discount ID
     * @param discountRequest The updated discount data
     * @return ResponseEntity with the updated discount
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountResponse> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody DiscountRequest discountRequest) {

        Discount discount = discountService.updateDiscount(id, discountRequest);
        return ResponseEntity.ok(DiscountResponse.fromEntity(discount));
    }

    /**
     * Gets all discounts with pagination (admin only).
     *
     * @param page The page number
     * @param size The page size
     * @return ResponseEntity with paged discounts
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<DiscountResponse>> getAllDiscounts(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        PagedResponse<DiscountResponse> response = discountService.getAllDiscountsPaged(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all active discounts.
     *
     * @return ResponseEntity with active discounts
     */
    @GetMapping("/active")
    public ResponseEntity<List<DiscountResponse>> getActiveDiscounts() {
        List<Discount> discounts = discountService.getActiveDiscounts();
        List<DiscountResponse> response = discounts.stream()
                .map(DiscountResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Validates a discount code.
     *
     * @param code The discount code to validate
     * @return ResponseEntity with the discount
     */
    @GetMapping("/validate/{code}")
    public ResponseEntity<DiscountResponse> validateDiscountCode(@PathVariable String code) {
        Discount discount = discountService.validateDiscountCode(code);
        return ResponseEntity.ok(DiscountResponse.fromEntity(discount));
    }
}