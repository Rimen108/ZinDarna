
package com.zindarna.orderservice.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SupplierOrder {

    private String supplierId;
    private List<String> productIds = new ArrayList<>();
    private String supplierOrderStatus;
    private LocalDateTime orderDate;

    public SupplierOrder() {
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds != null
                ? productIds
                : new ArrayList<>();
    }

    public String getSupplierOrderStatus() {
        return supplierOrderStatus;
    }

    public void setSupplierOrderStatus(String supplierOrderStatus) {
        this.supplierOrderStatus = supplierOrderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
