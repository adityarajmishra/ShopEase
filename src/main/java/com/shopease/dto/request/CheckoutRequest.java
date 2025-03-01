package com.shopease.dto.request;

import lombok.Data;

/**
 * DTO for handling checkout requests.
 */
@Data
public class CheckoutRequest {
    private String discountCode;
}