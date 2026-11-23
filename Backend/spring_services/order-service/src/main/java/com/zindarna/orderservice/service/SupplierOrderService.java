
package com.zindarna.orderservice.service;

import com.zindarna.orderservice.exception.ResourceNotFoundException;
import com.zindarna.orderservice.model.Order;
import com.zindarna.orderservice.model.SupplierOrder;
import com.zindarna.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import com.zindarna.orderservice.dto.SupplierOrderResponse;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Map;

@Service
public class SupplierOrderService {

    private final OrderRepository orderRepository;

    private static final Set<String> VALID_STATUSES = Set.of(
            "PENDING",
            "CONFIRMED",
            "PREPARING",
            "SHIPPED",
            "DELIVERED"
    );

    private static final Map<String, String> ALLOWED_TRANSITIONS = Map.of(
            "PENDING", "CONFIRMED",
            "CONFIRMED", "PREPARING",
            "PREPARING", "SHIPPED",
            "SHIPPED", "DELIVERED"
    );

    public SupplierOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Retrieve only the authenticated supplier's part of a customer order
    public List<SupplierOrder> getSupplierOrders(
            String orderId,
            String supplierId
    ) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (supplierId == null || supplierId.isBlank()) {
            throw new IllegalArgumentException("Supplier ID is required");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: " + orderId
                        )
                );

        if (order.getSupplierOrders() == null) {
            throw new ResourceNotFoundException(
                    "Supplier order not found for this order"
            );
        }

        SupplierOrder supplierOrder = order.getSupplierOrders()
                .stream()
                .filter(so -> supplierId.equals(so.getSupplierId()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier order not found for this supplier"
                        )
                );

        // Return only this supplier's order
        return List.of(supplierOrder);
    }

    // Retrieve customer orders containing products from this supplier
    // Retrieve only the authenticated supplier's customer orders
    public List<SupplierOrderResponse> getOrdersBySupplier(String supplierId) {

        if (supplierId == null || supplierId.isBlank()) {
            throw new IllegalArgumentException("Supplier ID is required");
        }

        return orderRepository.findBySupplierOrdersSupplierId(supplierId)
                .stream()
                .map(order -> {

                    // Find this supplier's sub-order
                    SupplierOrder supplierOrder = order.getSupplierOrders()
                            .stream()
                            .filter(so -> supplierId.equals(so.getSupplierId()))
                            .findFirst()
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Supplier order not found"
                                    )
                            );

                    // Keep only products belonging to this supplier
                    List<SupplierOrderResponse.ProductItemResponse> products =
                            order.getItems() == null
                                    ? List.of()
                                    : order.getItems().stream()
                                    .filter(item ->
                                            supplierId.equals(item.getSupplierId())
                                    )
                                    .map(item ->
                                            new SupplierOrderResponse.ProductItemResponse(
                                                    item.getProductId(),
                                                    item.getProductName(),
                                                    item.getUnitPrice(),
                                                    item.getQuantity(),
                                                    item.getSubtotal()
                                            )
                                    )
                                    .toList();

                    return new SupplierOrderResponse(
                            order.getId(),
                            supplierOrder.getOrderDate(),
                            supplierOrder.getSupplierOrderStatus(),
                            products
                    );
                })
                .toList();
    }

    // Update the status of this supplier's part of an order
    public SupplierOrderResponse updateSupplierOrderStatus(
            String orderId,
            String supplierId,
            String newStatus
    ) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (supplierId == null || supplierId.isBlank()) {
            throw new IllegalArgumentException("Supplier ID is required");
        }

        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException(
                    "Supplier order status is required"
            );
        }

        String normalizedStatus = newStatus.trim().toUpperCase(Locale.ROOT);

        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new IllegalArgumentException(
                    "Invalid supplier order status. Allowed values: "
                            + VALID_STATUSES
            );
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: " + orderId
                        )
                );

        if (order.getSupplierOrders() == null) {
            throw new ResourceNotFoundException(
                    "Supplier order not found for this order"
            );
        }

        // Find only the authenticated supplier's sub-order
        SupplierOrder supplierOrder = order.getSupplierOrders()
                .stream()
                .filter(so -> supplierId.equals(so.getSupplierId()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier order not found for this supplier"
                        )
                );

        String currentStatus = supplierOrder.getSupplierOrderStatus();

        if (currentStatus == null || currentStatus.isBlank()) {
            throw new IllegalStateException(
                    "Current supplier order status is missing"
            );
        }

        currentStatus = currentStatus.trim().toUpperCase(Locale.ROOT);

        String expectedStatus = ALLOWED_TRANSITIONS.get(currentStatus);

        if (expectedStatus == null ||
                !expectedStatus.equals(normalizedStatus)) {

            throw new IllegalStateException(
                    "Invalid status transition. Current status: "
                            + currentStatus
                            + ". Allowed next status: "
                            + (expectedStatus != null ? expectedStatus : "None")
            );
        }

        supplierOrder.setSupplierOrderStatus(normalizedStatus);
        // Update the main order status when all supplier orders are delivered
        boolean allDelivered = order.getSupplierOrders()
                .stream()
                .allMatch(so -> "DELIVERED".equalsIgnoreCase(
                        so.getSupplierOrderStatus()
                ));

        if (allDelivered) {
            order.setOrderStatus("DELIVERED");
        }

        Order savedOrder = orderRepository.save(order);

// Keep only products belonging to this supplier
        List<SupplierOrderResponse.ProductItemResponse> products =
                savedOrder.getItems() == null
                        ? List.of()
                        : savedOrder.getItems().stream()
                        .filter(item -> supplierId.equals(item.getSupplierId()))
                        .map(item -> new SupplierOrderResponse.ProductItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getUnitPrice(),
                                item.getQuantity(),
                                item.getSubtotal()
                        ))
                        .toList();

// Return only this supplier's information
        return new SupplierOrderResponse(
                savedOrder.getId(),
                supplierOrder.getOrderDate(),
                supplierOrder.getSupplierOrderStatus(),
                products
        );
    }
}
