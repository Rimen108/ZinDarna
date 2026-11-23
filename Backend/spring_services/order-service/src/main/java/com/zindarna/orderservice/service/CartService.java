
package com.zindarna.orderservice.service;

import com.zindarna.orderservice.dto.CatalogProductResponse;
import com.zindarna.orderservice.model.Cart;
import com.zindarna.orderservice.model.CartItem;
import com.zindarna.orderservice.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CatalogClient catalogClient;

    public CartService(
            CartRepository cartRepository,
            CatalogClient catalogClient
    ) {
        this.cartRepository = cartRepository;
        this.catalogClient = catalogClient;
    }

    public Cart getOrCreateCart(String userId, String projectId) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (projectId == null || projectId.isBlank()) {
            return cartRepository
                    .findByUserIdAndProjectIdIsNull(userId)
                    .orElseGet(() ->
                            cartRepository.save(new Cart(userId, null))
                    );
        }

        return cartRepository
                .findByUserIdAndProjectId(userId, projectId)
                .orElseGet(() ->
                        cartRepository.save(new Cart(userId, projectId))
                );
    }

    public Cart getCart(String userId, String projectId) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (projectId == null || projectId.isBlank()) {
            return cartRepository
                    .findByUserIdAndProjectIdIsNull(userId)
                    .orElseThrow(() ->
                            new IllegalStateException("Cart not found")
                    );
        }

        return cartRepository
                .findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() ->
                        new IllegalStateException("Cart not found")
                );
    }

    // Add a product to the cart using Catalog Service information
    public Cart addItem(
            String userId,
            String projectId,
            String productId,
            int quantity
    ) {

        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID is required");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        // Retrieve the real product information from Catalog Service
        CatalogProductResponse product =
                catalogClient.getProductById(productId);

        if (product == null) {
            throw new IllegalStateException("Product not found");
        }

        if (!product.isAvailable()) {
            throw new IllegalStateException("Product is not available");
        }

        // Find or create the customer's cart
        Cart cart = getOrCreateCart(userId, projectId);

        // Check whether this product is already in the cart
        for (CartItem item : cart.getItems()) {

            if (productId.equals(item.getProductId())) {

                item.setQuantity(item.getQuantity() + quantity);
                item.setProductName(product.getName());
                item.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
                item.setSupplierId(product.getSupplierId());

                return cartRepository.save(cart);
            }
        }

        // Convert the Catalog product into a cart item
        CartItem cartItem = new CartItem(
                product.getId(),
                product.getName(),
                BigDecimal.valueOf(product.getPrice()),
                quantity,
                product.getSupplierId()
        );

        cart.addItem(cartItem);

        return cartRepository.save(cart);
    }

    // Remove a product from the cart
    public Cart removeItem(
            String userId,
            String projectId,
            String productId
    ) {

        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID is required");
        }

        Cart cart = getCart(userId, projectId);

        cart.removeItem(productId);

        return cartRepository.save(cart);
    }

    public Cart clearCart(String userId, String projectId) {

        Cart cart = getCart(userId, projectId);

        cart.getItems().clear();

        return cartRepository.save(cart);
    }

    public Cart updateItemQuantity(
            String userId,
            String projectId,
            String productId,
            int quantity
    ) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException(
                    "Product ID is required"
            );
        }

        Cart cart = getCart(userId, projectId);

        boolean itemFound = false;

        for (var item : cart.getItems()) {

            if (productId.equals(item.getProductId())) {
                item.setQuantity(quantity);
                itemFound = true;
                break;
            }
        }

        if (!itemFound) {
            throw new IllegalStateException(
                    "Product not found in cart"
            );
        }

        return cartRepository.save(cart);
    }
}
