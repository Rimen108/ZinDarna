
package com.zindarna.orderservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private String paymentMethod;
    private BigDecimal amount;
    private String paymentStatus;
    private String transactionReference;
    private LocalDateTime paymentDate;

    public Payment() {
    }

    public Payment(String paymentMethod, BigDecimal amount) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentStatus = "PENDING";
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    public void markAsPaid() {
        this.paymentStatus = "PAID";
        this.paymentDate = LocalDateTime.now();
    }
    public void markAsFailed() {
        this.paymentStatus = "FAILED";
    }
}
