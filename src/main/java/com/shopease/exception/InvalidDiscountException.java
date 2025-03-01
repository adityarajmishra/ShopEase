package com.shopease.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a discount code is invalid or expired.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDiscountException extends RuntimeException {

    public InvalidDiscountException(String message) {
        super(message);
    }
}