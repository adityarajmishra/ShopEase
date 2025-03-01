package com.shopease.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for handling discount creation and update requests.
 */
@Data
public class DiscountRequest {

    @NotBlank(message = "Discount code cannot be blank")
    @Pattern(regexp = "^[A-Z0-9]{3,20}$", message = "Discount code must be 3-20 uppercase letters and numbers")
    private String code;

    @NotNull(message = "Percentage cannot be null")
    @DecimalMin(value = "0.01", message = "Percentage must be greater than 0")
    @DecimalMax(value = "100.00", message = "Percentage cannot exceed 100")
    private BigDecimal percentage;

    @NotNull(message = "Start date cannot be null")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "Expiry date cannot be null")
    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;

    @NotNull(message = "Max usage cannot be null")
    @Positive(message = "Max usage must be positive")
    private Integer maxUsage;
}