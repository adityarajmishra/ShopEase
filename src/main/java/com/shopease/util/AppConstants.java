package com.shopease.util;

/**
 * Application-wide constants.
 */
public final class AppConstants {

    // General
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // Auth related
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    // Role constants
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Order status
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_COMPLETED = "COMPLETED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    // Product status
    public static final String PRODUCT_STATUS_ACTIVE = "ACTIVE";
    public static final String PRODUCT_STATUS_OUT_OF_STOCK = "OUT_OF_STOCK";
    public static final String PRODUCT_STATUS_DISCONTINUED = "DISCONTINUED";

    // Payment status
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_SUCCESSFUL = "SUCCESSFUL";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";

    // Time constants
    public static final long MILLIS_IN_DAY = 86400000; // 24 * 60 * 60 * 1000

    private AppConstants() {
        // Utility class, should not be instantiated
        throw new IllegalStateException("Utility class");
    }
}