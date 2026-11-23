
package com.zindarna.orderservice.controller;

import com.zindarna.orderservice.model.Order;
import com.zindarna.orderservice.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Process a simulated payment
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/{orderId}")
    public ResponseEntity<Order> processPayment(
            @PathVariable String orderId,
            @RequestParam String paymentMethod,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        Order order = paymentService.processPayment(
                orderId,
                paymentMethod,
                userId
        );

        return ResponseEntity.ok(order);
    }
}
