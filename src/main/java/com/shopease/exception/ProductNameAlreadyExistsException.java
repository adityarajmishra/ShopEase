package com.shopease.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a product with a name that already exists.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductNameAlreadyExistsException extends RuntimeException {

    public ProductNameAlreadyExistsException(String message) {
        super(message);
    }
}