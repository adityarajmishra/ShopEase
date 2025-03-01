package com.shopease.service.impl;

import com.shopease.dto.request.DiscountRequest;
import com.shopease.dto.response.DiscountResponse;
import com.shopease.dto.response.PagedResponse;
import com.shopease.exception.InvalidDiscountException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.Discount;
import com.shopease.repository.DiscountRepository;
import com.shopease.service.DiscountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the DiscountService interface.
 */
@Service
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    @Transactional
    public Discount createDiscount(DiscountRequest discountRequest) {
        validateDiscountData(discountRequest, null);

        Discount discount = new Discount();
        discount.setCode(discountRequest.getCode());
        discount.setPercentage(discountRequest.getPercentage());
        discount.setStartDate(discountRequest.getStartDate());
        discount.setExpiryDate(discountRequest.getExpiryDate());
        discount.setMaxUsage(discountRequest.getMaxUsage());
        discount.setCurrentUsage(0);

        return discountRepository.save(discount);
    }

    @Override
    @Transactional
    public Discount updateDiscount(Long id, DiscountRequest discountRequest) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + id));

        validateDiscountData(discountRequest, discount);

        discount.setCode(discountRequest.getCode());
        discount.setPercentage(discountRequest.getPercentage());
        discount.setStartDate(discountRequest.getStartDate());
        discount.setExpiryDate(discountRequest.getExpiryDate());
        discount.setMaxUsage(discountRequest.getMaxUsage());

        return discountRepository.save(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public Discount validateDiscountCode(String code) {
        Discount discount = discountRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Discount code not found: " + code));

        if (!discount.isValid()) {
            throw new InvalidDiscountException("Discount code is expired or exceeded usage limit");
        }

        return discount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DiscountResponse> getAllDiscountsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("expiryDate").ascending());
        Page<Discount> discounts = discountRepository.findAll(pageable);

        return new PagedResponse<>(
                discounts.getContent().stream().map(DiscountResponse::fromEntity).collect(Collectors.toList()),
                discounts.getNumber(),
                discounts.getSize(),
                discounts.getTotalElements(),
                discounts.getTotalPages(),
                discounts.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Discount> getActiveDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        return discountRepository.findByStartDateBeforeAndExpiryDateAfterAndCurrentUsageLessThanMaxUsage(
                now, now, Integer.MAX_VALUE);
    }

    /**
     * Validates discount data when creating or updating a discount.
     *
     * @param discountRequest The discount data to validate
     * @param existingDiscount The existing discount (null for new discounts)
     */
    private void validateDiscountData(DiscountRequest discountRequest, Discount existingDiscount) {
        // Validate code uniqueness
        if (existingDiscount == null || !existingDiscount.getCode().equals(discountRequest.getCode())) {
            if (discountRepository.existsByCode(discountRequest.getCode())) {
                throw new InvalidDiscountException("Discount code already exists: " + discountRequest.getCode());
            }
        }

        // Validate percentage
        if (discountRequest.getPercentage().compareTo(BigDecimal.ZERO) < 0 ||
                discountRequest.getPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidDiscountException("Discount percentage must be between 0 and 100");
        }

        // Validate dates
        if (discountRequest.getStartDate().isAfter(discountRequest.getExpiryDate())) {
            throw new InvalidDiscountException("Start date must be before expiry date");
        }

        // Validate max usage
        if (discountRequest.getMaxUsage() <= 0) {
            throw new InvalidDiscountException("Max usage must be positive");
        }
    }
}