
package com.zindarna.orderservice.model;

import java.math.BigDecimal;

public class CartItem {

    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;
    private String supplierId;

    public CartItem() {
    }

    public CartItem(String productId, String productName,
                    BigDecimal unitPrice, int quantity,
                    String supplierId) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.supplierId = supplierId;
    }

    public BigDecimal getSubtotal() {
        if (unitPrice == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }

        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
}
