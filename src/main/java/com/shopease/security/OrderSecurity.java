package com.shopease.security;

import com.shopease.model.Order;
import com.shopease.repository.OrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Component for checking order-related permissions.
 */
@Component("orderSecurity")
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public OrderSecurity(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Checks if the authenticated user is the owner of an order.
     *
     * @param authentication The authentication object
     * @param orderId The order ID to check
     * @return true if the user is the owner, false otherwise
     */
    public boolean isOrderOwner(Authentication authentication, Long orderId) {
        if (authentication == null) {
            return false;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            return false;
        }

        return order.get().getUser().getId().equals(userPrincipal.getId());
    }
}