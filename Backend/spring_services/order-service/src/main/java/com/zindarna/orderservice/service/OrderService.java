
package com.zindarna.orderservice.service;

import com.zindarna.orderservice.exception.ResourceNotFoundException;
import com.zindarna.orderservice.model.Cart;
import com.zindarna.orderservice.model.CartItem;
import com.zindarna.orderservice.model.Order;
import com.zindarna.orderservice.model.OrderItem;
import com.zindarna.orderservice.model.SupplierOrder;
import com.zindarna.orderservice.repository.OrderRepository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartService cartService
    ) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    // Retrieve an order using its ID
    public Order getOrderById(String orderId, String userId) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        Order order = orderRepository.findByIdAndUserId(orderId, userId);

        if (order == null) {
            throw new ResourceNotFoundException(
                    "Order not found with ID: " + orderId
            );
        }

        return order;
    }

    // Retrieve all orders belonging to a user
    public List<Order> getOrdersByUser(String userId) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        return orderRepository.findByUserId(userId);
    }

    // Retrieve orders belonging to a user with a specific status
    public List<Order> getOrdersByUserAndStatus(
            String userId,
            String status
    ) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Order status is required");
        }

        return orderRepository.findByUserIdAndOrderStatus(
                userId,
                status
        );
    }

    // Retrieve all orders (for authorized administration)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Create an order from the user's cart
    public Order checkout(
            String userId,
            String projectId,
            String checkoutKey
    ) {

        // Validate user ID
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        // Validate checkout key
        if (checkoutKey == null || checkoutKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Checkout key is required"
            );
        }

        // Check whether this checkout was already processed
        Optional<Order> existingOrder =
                orderRepository.findByCheckoutKeyAndUserId(
                        checkoutKey,
                        userId
                );

        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        // Retrieve the user's cart
        Cart cart = cartService.getCart(userId, projectId);

        // Check that the cart is not empty
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot checkout an empty cart"
            );
        }

        // Create a new order
        Order order = new Order();

        order.setUserId(userId);
        order.setProjectId(projectId);
        order.setCheckoutKey(checkoutKey);

        // Convert cart items into order items
        List<OrderItem> orderItems = new ArrayList<>();

        // Group products by their supplier ID
        Map<String, SupplierOrder> supplierOrderMap =
                new LinkedHashMap<>();

        for (CartItem cartItem : cart.getItems()) {

            // Get the supplier ID
            String supplierId = cartItem.getSupplierId();

            if (supplierId == null || supplierId.isBlank()) {
                throw new IllegalStateException(
                        "Supplier ID is missing for product: "
                                + cartItem.getProductId()
                );
            }

            // Create an order item
            OrderItem orderItem = new OrderItem(
                    cartItem.getProductId(),
                    cartItem.getProductName(),
                    cartItem.getUnitPrice(),
                    cartItem.getQuantity(),
                    supplierId
            );

            orderItems.add(orderItem);

            // Find or create the supplier order
            SupplierOrder supplierOrder =
                    supplierOrderMap.get(supplierId);

            if (supplierOrder == null) {

                supplierOrder = new SupplierOrder();

                supplierOrder.setSupplierId(supplierId);
                supplierOrder.setSupplierOrderStatus("PENDING");
                supplierOrder.setOrderDate(LocalDateTime.now());

                supplierOrderMap.put(supplierId, supplierOrder);
            }

            // Add the product ID to this supplier's order
            supplierOrder.getProductIds().add(
                    cartItem.getProductId()
            );
        }

        // Add items to the main order
        order.setItems(orderItems);

        // Add supplier orders to the main order
        order.setSupplierOrders(
                new ArrayList<>(supplierOrderMap.values())
        );

        // Calculate the total amount
        order.updateTotalAmount();

        // Set initial order information
        order.setOrderStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        // Save the order in MongoDB
        Order savedOrder;

        try {

            savedOrder = orderRepository.save(order);

        } catch (DuplicateKeyException exception) {

            // Another simultaneous request may have saved
            // an order with the same checkout key.

            return orderRepository
                    .findByCheckoutKeyAndUserId(
                            checkoutKey,
                            userId
                    )
                    .orElseThrow(() -> exception);
        }

        // Clear the cart only after the order has been saved
        cartService.clearCart(userId, projectId);

        return savedOrder;
    }
}
