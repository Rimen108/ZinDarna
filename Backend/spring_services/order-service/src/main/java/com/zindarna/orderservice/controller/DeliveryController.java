package com.zindarna.orderservice.controller;


import com.zindarna.orderservice.model.DeliveryTracking;
import com.zindarna.orderservice.model.Order;
import com.zindarna.orderservice.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }
    // Create delivery tracking for an order
    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateDelivery(
            @PathVariable String orderId,
            @RequestParam String deliveryAddress,
            @RequestParam
            @org.springframework.format.annotation.DateTimeFormat(
                    iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime estimatedDeliveryDate
    ) {

        Order order = deliveryService.updateDelivery(
                orderId,
                deliveryAddress,
                estimatedDeliveryDate
        );

        return ResponseEntity.ok(order);
    }
    // Update delivery status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateDeliveryStatus(
            @PathVariable String orderId,
            @RequestParam String deliveryStatus
    ) {

        Order order = deliveryService.updateDeliveryStatus(
                orderId,
                deliveryStatus
        );

        return ResponseEntity.ok(order);
    }
    // Get delivery tracking information
    @GetMapping("/{orderId}")
    public ResponseEntity<DeliveryTracking> getDeliveryTracking(
            @PathVariable String orderId
    ) {

        DeliveryTracking tracking =
                deliveryService.getDeliveryTracking(orderId);

        return ResponseEntity.ok(tracking);
    }
}
