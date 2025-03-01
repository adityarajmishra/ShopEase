package com.shopease.event;

import com.shopease.model.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event triggered when an order is created.
 */
@Getter
public class OrderCreatedEvent extends ApplicationEvent {

    private final Order order;

    public OrderCreatedEvent(Order order) {
        super(order);
        this.order = order;
    }
}
