
package com.zindarna.orderservice.service;

import com.zindarna.orderservice.exception.ResourceNotFoundException;
import com.zindarna.orderservice.model.DeliveryTracking;
import com.zindarna.orderservice.model.Order;
import com.zindarna.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class DeliveryService {

    private static final Set<String> VALID_DELIVERY_STATUSES = Set.of(
            "PROCESSING",
            "SHIPPED",
            "IN_TRANSIT",
            "DELIVERED"
    );

    private final OrderRepository orderRepository;

    public DeliveryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create delivery tracking for a paid order
    public Order updateDelivery(
            String orderId,
            String deliveryAddress,
            LocalDateTime estimatedDeliveryDate
    ) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            throw new IllegalArgumentException("Delivery address is required");
        }

        if (estimatedDeliveryDate == null) {
            throw new IllegalArgumentException(
                    "Estimated delivery date is required"
            );
        }

        // Find the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: " + orderId
                        )
                );

        // Check that payment is successful
        if (order.getPayment() == null ||
                !"PAID".equalsIgnoreCase(order.getPayment().getPaymentStatus())) {
            throw new IllegalStateException(
                    "Delivery tracking can only be created for paid orders"
            );
        }

        // Prevent replacing existing delivery tracking
        if (order.getDeliveryTracking() != null) {
            throw new IllegalStateException(
                    "Delivery tracking already exists for this order"
            );
        }

        // Create delivery tracking
        DeliveryTracking tracking = new DeliveryTracking();

        tracking.setTrackingNumber("TRK-" + UUID.randomUUID());
        tracking.setDeliveryAddress(deliveryAddress);
        tracking.setDeliveryStatus("PROCESSING");
        tracking.setEstimatedDeliveryDate(estimatedDeliveryDate);

        order.setDeliveryTracking(tracking);

        return orderRepository.save(order);
    }

    // Update the delivery status

    public Order updateDeliveryStatus(
            String orderId,
            String deliveryStatus
    ) {

        // Validate the order ID
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        // Validate the new delivery status
        if (deliveryStatus == null || deliveryStatus.isBlank()) {
            throw new IllegalArgumentException(
                    "Delivery status is required"
            );
        }

        // Normalize the status
        String normalizedStatus = deliveryStatus
                .trim()
                .toUpperCase(Locale.ROOT);

        // Check whether the status is valid
        if (!VALID_DELIVERY_STATUSES.contains(normalizedStatus)) {
            throw new IllegalArgumentException(
                    "Invalid delivery status. Allowed values: "
                            + VALID_DELIVERY_STATUSES
            );
        }

        // Find the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: " + orderId
                        )
                );

        // Retrieve delivery tracking
        DeliveryTracking tracking = order.getDeliveryTracking();

        if (tracking == null) {
            throw new IllegalStateException(
                    "Delivery tracking has not been created for this order"
            );
        }

        // Get the current status
        String currentStatus = tracking.getDeliveryStatus();

        // Determine the only allowed next status
        String expectedNextStatus;

        switch (currentStatus) {
            case "PROCESSING":
                expectedNextStatus = "SHIPPED";
                break;

            case "SHIPPED":
                expectedNextStatus = "IN_TRANSIT";
                break;

            case "IN_TRANSIT":
                expectedNextStatus = "DELIVERED";
                break;

            case "DELIVERED":
                throw new IllegalStateException(
                        "Delivery has already been completed"
                );

            default:
                throw new IllegalStateException(
                        "Current delivery status is invalid"
                );
        }

        // Prevent invalid status transitions
        if (!expectedNextStatus.equals(normalizedStatus)) {
            throw new IllegalStateException(
                    "Invalid status transition. The next allowed status is: "
                            + expectedNextStatus
            );
        }

        // Update the status
        tracking.setDeliveryStatus(normalizedStatus);

        // Record the actual delivery date
        if ("DELIVERED".equals(normalizedStatus)) {
            tracking.setActualDeliveryDate(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }


    // Get delivery information for an order
    public DeliveryTracking getDeliveryTracking(String orderId) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        // Find the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: " + orderId
                        )
                );

        DeliveryTracking tracking = order.getDeliveryTracking();

        if (tracking == null) {
            throw new IllegalStateException(
                    "Delivery tracking not found"
            );
        }

        return tracking;
    }
}
