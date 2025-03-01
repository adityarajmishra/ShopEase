package com.shopease.dto.response;

import com.shopease.model.Discount;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for returning discount information in API responses.
 */
@Data
public class DiscountResponse {
    private Long id;
    private String code;
    private BigDecimal percentage;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private Integer maxUsage;
    private Integer currentUsage;
    private boolean active;

    /**
     * Creates a DiscountResponse from a Discount entity.
     *
     * @param discount The discount entity
     * @return A DiscountResponse DTO
     */
    public static DiscountResponse fromEntity(Discount discount) {
        DiscountResponse response = new DiscountResponse();
        response.setId(discount.getId());
        response.setCode(discount.getCode());
        response.setPercentage(discount.getPercentage());
        response.setStartDate(discount.getStartDate());
        response.setExpiryDate(discount.getExpiryDate());
        response.setMaxUsage(discount.getMaxUsage());
        response.setCurrentUsage(discount.getCurrentUsage());
        response.setActive(discount.isValid());
        return response;
    }
}