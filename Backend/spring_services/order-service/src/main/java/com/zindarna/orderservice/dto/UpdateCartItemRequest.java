
package com.zindarna.orderservice.dto;

import jakarta.validation.constraints.Positive;

public class UpdateCartItemRequest {

    @Positive(message = "Quantity must be greater than zero")
    private int quantity;

    public UpdateCartItemRequest() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
