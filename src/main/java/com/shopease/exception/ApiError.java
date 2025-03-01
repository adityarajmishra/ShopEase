package com.shopease.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an API error to be returned in error responses.
 */
@Data
public class ApiError {
    private HttpStatus status;
    private String message;
    private LocalDateTime timestamp;
    private List<String> details = new ArrayList<>();

    public ApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public void addDetail(String detail) {
        this.details.add(detail);
    }
}
