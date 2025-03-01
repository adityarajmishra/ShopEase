package com.shopease.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listeners for various application events.
 */
@Component
public class EventListeners {

    private static final Logger logger = LoggerFactory.getLogger(EventListeners.class);

    /**
     * Handles cart expired events.
     *
     * @param event The cart expired event
     */
    @EventListener
    public void handleCartExpiredEvent(CartExpiredEvent event) {
        logger.info("Cart expired for user {}: {}",
                event.getCart().getUser().getId(),
                event.getCart().getId());

        // In a real application, you might want to:
        // - Send an email to the user about their abandoned cart
        // - Store analytics about abandoned carts
        // - Move items back to inventory if they were reserved
    }

    /**
     * Handles order created events.
     *
     * @param event The order created event
     */
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        logger.info("Order created: {} for user {}",
                event.getOrder().getId(),
                event.getOrder().getUser().getId());

        // In a real application, you might want to:
        // - Send an order confirmation email
        // - Notify the warehouse/fulfillment system
        // - Update inventory systems
    }

    /**
     * Handles order completed events.
     *
     * @param event The order completed event
     */
    @EventListener
    public void handleOrderCompletedEvent(OrderCompletedEvent event) {
        logger.info("Order completed: {} for user {}",
                event.getOrder().getId(),
                event.getOrder().getUser().getId());

        // In a real application, you might want to:
        // - Send a payment confirmation email
        // - Notify the shipping department
        // - Update sales reports
    }

    /**
     * Handles order cancelled events.
     *
     * @param event The order cancelled event
     */
    @EventListener
    public void handleOrderCancelledEvent(OrderCancelledEvent event) {
        logger.info("Order cancelled: {} for user {}",
                event.getOrder().getId(),
                event.getOrder().getUser().getId());

        // In a real application, you might want to:
        // - Send a cancellation confirmation email
        // - Issue a refund if payment was already made
        // - Update inventory systems
    }
}