package com.shopease.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopease.util.AppConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user in the system.
 * Implements role-based access control with two roles: USER and ADMIN.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Cart cart;

    /**
     * Enum for user roles with their respective authorities.
     */
    public enum Role {
        ADMIN(AppConstants.ROLE_ADMIN),
        USER(AppConstants.ROLE_USER);

        private final String roleName;

        Role(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleName() {
            return this.roleName;
        }
    }

    /**
     * Factory method to create a regular user.
     *
     * @param name User's name
     * @param email User's email
     * @param encryptedPassword User's encrypted password
     * @return A new User entity with USER role
     */
    public static User createUser(String name, String email, String encryptedPassword) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encryptedPassword);
        user.setRole(Role.USER);
        return user;
    }

    /**
     * Factory method to create an admin user.
     *
     * @param name Admin's name
     * @param email Admin's email
     * @param encryptedPassword Admin's encrypted password
     * @return A new User entity with ADMIN role
     */
    public static User createAdmin(String name, String email, String encryptedPassword) {
        User user = createUser(name, email, encryptedPassword);
        user.setRole(Role.ADMIN);
        return user;
    }

    /**
     * Convenience method to check if user has admin role.
     *
     * @return true if user is an admin, false otherwise
     */
    @Transient
    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }
}