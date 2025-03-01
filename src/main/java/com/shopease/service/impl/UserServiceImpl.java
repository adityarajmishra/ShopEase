package com.shopease.service.impl;

import com.shopease.exception.EmailAlreadyInUseException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.User;
import com.shopease.repository.UserRepository;
import com.shopease.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserService interface.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerUser(String name, String email, String password) {
        validateUserRegistration(email);

        String encryptedPassword = passwordEncoder.encode(password);
        User user = User.createUser(name, email, encryptedPassword);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User registerAdmin(String name, String email, String password) {
        validateUserRegistration(email);

        String encryptedPassword = passwordEncoder.encode(password);
        User admin = User.createAdmin(name, email, encryptedPassword);

        return userRepository.save(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Validates a user registration by checking if the email is already in use.
     *
     * @param email The email to validate
     * @throws EmailAlreadyInUseException if the email is already in use
     */
    private void validateUserRegistration(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException("Email is already in use: " + email);
        }
    }
}