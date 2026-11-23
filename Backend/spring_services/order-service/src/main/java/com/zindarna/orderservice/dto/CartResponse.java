
package com.zindarna.orderservice.dto;

import com.zindarna.orderservice.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    private String id;
    private String projectId;
    private List<CartItem> items;
    private BigDecimal totalPrice;

    public CartResponse() {
    }

    public CartResponse(
            String id,
            String projectId,
            List<CartItem> items,
            BigDecimal totalPrice
    ) {
        this.id = id;
        this.projectId = projectId;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
