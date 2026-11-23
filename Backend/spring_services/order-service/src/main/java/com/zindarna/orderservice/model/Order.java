
package com.zindarna.orderservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "orders")
public class Order {
    @Indexed(unique = true, sparse = true)
    private String checkoutKey;

    @Id
    private String id;

    private String userId;
    private String projectId;

    private List<OrderItem> items = new ArrayList<>();

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String orderStatus;

    private Payment payment;

    private List<SupplierOrder> supplierOrders = new ArrayList<>();

    private DeliveryTracking deliveryTracking;

    private Invoice invoice;

    private LocalDateTime orderDate;

    public Order() {
    }

    public BigDecimal calculateTotalAmount() {
        if (items == null) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateTotalAmount() {
        this.totalAmount = calculateTotalAmount();
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null
                ? totalAmount
                : BigDecimal.ZERO;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<SupplierOrder> getSupplierOrders() {
        return supplierOrders;
    }

    public void setSupplierOrders(List<SupplierOrder> supplierOrders) {
        this.supplierOrders = supplierOrders != null
                ? supplierOrders
                : new ArrayList<>();
    }

    public DeliveryTracking getDeliveryTracking() {
        return deliveryTracking;
    }

    public void setDeliveryTracking(DeliveryTracking deliveryTracking) {
        this.deliveryTracking = deliveryTracking;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    public String getCheckoutKey() {
        return checkoutKey;
    }

    public void setCheckoutKey(String checkoutKey) {
        this.checkoutKey = checkoutKey;
    }
}
