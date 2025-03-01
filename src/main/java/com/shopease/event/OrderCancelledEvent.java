package com.shopease.event;

import com.shopease.model.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event triggered when an order is cancelled.
 */
@Getter
public class OrderCancelledEvent extends ApplicationEvent {

    private final Order order;

    public OrderCancelledEvent(Order order) {
        super(order);
        this.order = order;
    }
}