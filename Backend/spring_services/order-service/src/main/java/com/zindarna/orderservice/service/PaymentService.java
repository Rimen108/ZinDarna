
package com.zindarna.orderservice.service;

import com.zindarna.orderservice.exception.ResourceNotFoundException;
import com.zindarna.orderservice.model.Order;
import com.zindarna.orderservice.model.Payment;
import com.zindarna.orderservice.model.Invoice;
import com.zindarna.orderservice.repository.OrderRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Process a simulated payment for an order
    public Order processPayment(
            String orderId,
            String paymentMethod,
            String userId
    ) {

        // Validate order ID
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException(
                    "Order ID is required"
            );
        }

        // Validate payment method
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException(
                    "Payment method is required"
            );
        }

        // Validate user ID
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }

        // Find the existing order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: " + orderId
                        )
                );

        // Verify that the order belongs to the authenticated user
        if (!userId.equals(order.getUserId())) {
            throw new AccessDeniedException(
                    "You are not allowed to pay for this order"
            );
        }

        // Prevent paying for an already-paid order
        if (order.getPayment() != null
                && "PAID".equalsIgnoreCase(
                order.getPayment().getPaymentStatus()
        )) {
            throw new IllegalStateException(
                    "This order has already been paid"
            );
        }

        // Get the order's total amount
        BigDecimal amount = order.getTotalAmount();

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "Order amount must be greater than zero"
            );
        }

        // Create a simulated payment
        Payment payment = new Payment(paymentMethod, amount);

        payment.setPaymentStatus("PAID");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionReference(
                "SIM-" + UUID.randomUUID()
        );

        // Attach payment information to the order
        order.setPayment(payment);

        // Generate the invoice after successful payment
        Invoice invoice = new Invoice();

        invoice.setInvoiceNumber(
                "INV-" + UUID.randomUUID()
        );
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setOrderId(order.getId());
        invoice.setItems(
                new ArrayList<>(order.getItems())
        );
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setPayment(payment);

        // Attach the invoice to the order
        order.setInvoice(invoice);

        // Save the updated order in MongoDB
        return orderRepository.save(order);
    }
}
