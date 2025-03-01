package com.shopease.service;

import com.shopease.model.User;

/**
 * Service interface for user-related operations.
 */
public interface UserService {

    /**
     * Registers a new user.
     *
     * @param name The user's name
     * @param email The user's email
     * @param password The user's plain text password
     * @return The created user
     */
    User registerUser(String name, String email, String password);

    /**
     * Registers a new admin user.
     *
     * @param name The admin's name
     * @param email The admin's email
     * @param password The admin's plain text password
     * @return The created admin user
     */
    User registerAdmin(String name, String email, String password);

    /**
     * Gets a user by ID.
     *
     * @param id The user ID
     * @return The user
     */
    User getUserById(Long id);

    /**
     * Gets a user by email.
     *
     * @param email The user email
     * @return The user
     */
    User getUserByEmail(String email);

    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}