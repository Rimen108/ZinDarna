
package com.zindarna.orderservice.controller;

import com.zindarna.orderservice.model.Order;
import com.zindarna.orderservice.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Get an order belonging to the authenticated user
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable String orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        Order order = orderService.getOrderById(orderId, userId);

        return ResponseEntity.ok(order);
    }

    // Get all orders belonging to the authenticated user
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        List<Order> orders = orderService.getOrdersByUser(userId);

        return ResponseEntity.ok(orders);
    }

    // Get the authenticated user's orders filtered by status
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/my-orders/status/{status}")
    public ResponseEntity<List<Order>> getMyOrdersByStatus(
            @PathVariable String status,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        List<Order> orders =
                orderService.getOrdersByUserAndStatus(userId, status);

        return ResponseEntity.ok(orders);
    }

    // Create an order using the authenticated user's cart
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(
            @RequestParam(required = false) String projectId,
            @RequestHeader("Idempotency-Key") String checkoutKey,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        Order order = orderService.checkout(
                userId,
                projectId,
                checkoutKey
        );

        return ResponseEntity.status(201).body(order);
    }
    // Get all orders (ADMIN only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {

        List<Order> orders = orderService.getAllOrders();

        return ResponseEntity.ok(orders);
    }
}
