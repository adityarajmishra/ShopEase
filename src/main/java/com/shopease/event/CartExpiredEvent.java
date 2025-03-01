package com.shopease.event;

import com.shopease.model.Cart;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event triggered when a cart expires.
 */
@Getter
public class CartExpiredEvent extends ApplicationEvent {

    private final Cart cart;

    public CartExpiredEvent(Cart cart) {
        super(cart);
        this.cart = cart;
    }
}