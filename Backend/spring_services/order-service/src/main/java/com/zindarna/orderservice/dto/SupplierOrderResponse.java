
package com.zindarna.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SupplierOrderResponse {

    private String orderId;
    private LocalDateTime orderDate;
    private String supplierOrderStatus;
    private List<ProductItemResponse> products;

    public SupplierOrderResponse() {
    }

    public SupplierOrderResponse(
            String orderId,
            LocalDateTime orderDate,
            String supplierOrderStatus,
            List<ProductItemResponse> products
    ) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.supplierOrderStatus = supplierOrderStatus;
        this.products = products;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getSupplierOrderStatus() {
        return supplierOrderStatus;
    }

    public void setSupplierOrderStatus(String supplierOrderStatus) {
        this.supplierOrderStatus = supplierOrderStatus;
    }

    public List<ProductItemResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductItemResponse> products) {
        this.products = products;
    }

    public static class ProductItemResponse {

        private String productId;
        private String productName;
        private BigDecimal unitPrice;
        private int quantity;
        private BigDecimal subtotal;

        public ProductItemResponse() {
        }

        public ProductItemResponse(
                String productId,
                String productName,
                BigDecimal unitPrice,
                int quantity,
                BigDecimal subtotal
        ) {
            this.productId = productId;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.subtotal = subtotal;
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

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }
    }
}
