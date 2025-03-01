package com.shopease.event;

import com.shopease.model.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event triggered when an order is completed.
 */
@Getter
public class OrderCompletedEvent extends ApplicationEvent {

    private final Order order;

    public OrderCompletedEvent(Order order) {
        super(order);
        this.order = order;
    }
}