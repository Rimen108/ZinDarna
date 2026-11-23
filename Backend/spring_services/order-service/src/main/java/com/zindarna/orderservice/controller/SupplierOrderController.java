
package com.zindarna.orderservice.controller;

import com.zindarna.orderservice.client.SupplierProfileClient;
import com.zindarna.orderservice.model.Order;
import com.zindarna.orderservice.model.SupplierOrder;
import com.zindarna.orderservice.service.SupplierOrderService;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;
import com.zindarna.orderservice.dto.SupplierOrderResponse;

import java.util.List;

@RestController
@RequestMapping("/api/supplier-orders")
public class SupplierOrderController {

    private final SupplierOrderService supplierOrderService;
    private final SupplierProfileClient supplierProfileClient;

    public SupplierOrderController(
            SupplierOrderService supplierOrderService,
            SupplierProfileClient supplierProfileClient
    ) {
        this.supplierOrderService = supplierOrderService;
        this.supplierProfileClient = supplierProfileClient;
    }

    // Retrieve this supplier's part of a customer order
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('SUPPLIER')")
    public List<SupplierOrder> getSupplierOrders(
            @PathVariable String orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String supplierId =
                supplierProfileClient.getApprovedSupplierProfileId(jwt);

        return supplierOrderService.getSupplierOrders(
                orderId,
                supplierId
        );
    }

    // Retrieve customer orders belonging to the authenticated supplier
    @GetMapping("/supplier")
    @PreAuthorize("hasRole('SUPPLIER')")
    public List<SupplierOrderResponse> getOrdersBySupplier(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String supplierId =
                supplierProfileClient.getApprovedSupplierProfileId(jwt);

        return supplierOrderService.getOrdersBySupplier(supplierId);
    }

    // Update the status of this supplier's part of an order
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('SUPPLIER')")
    public SupplierOrderResponse updateSupplierOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String supplierId =
                supplierProfileClient.getApprovedSupplierProfileId(jwt);

        return supplierOrderService.updateSupplierOrderStatus(
                orderId,
                supplierId,
                status
        );
    }
}
