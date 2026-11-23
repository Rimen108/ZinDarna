
package com.zindarna.orderservice.controller;

import com.zindarna.orderservice.dto.AddCartItemRequest;
import com.zindarna.orderservice.dto.UpdateCartItemRequest;
import com.zindarna.orderservice.model.Cart;
import com.zindarna.orderservice.service.CartService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@PreAuthorize("hasRole('CLIENT')")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get the authenticated client's cart
    @GetMapping
    public ResponseEntity<Cart> getCart(
            @RequestParam(required = false) String projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        Cart cart = cartService.getOrCreateCart(userId, projectId);

        return ResponseEntity.ok(cart);
    }

    // Add a product to the authenticated client's cart
    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(
            @RequestParam(required = false) String projectId,
            @Valid @RequestBody AddCartItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        Cart cart = cartService.addItem(
                userId,
                projectId,
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(cart);
    }

    // Remove a product from the authenticated client's cart
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeItem(
            @RequestParam(required = false) String projectId,
            @PathVariable String productId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        Cart cart = cartService.removeItem(
                userId,
                projectId,
                productId
        );

        return ResponseEntity.ok(cart);
    }

    // Clear the authenticated client's cart
    @DeleteMapping
    public ResponseEntity<Cart> clearCart(
            @RequestParam(required = false) String projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        Cart cart = cartService.clearCart(userId, projectId);

        return ResponseEntity.ok(cart);
    }

    // Update a product quantity in the authenticated client's cart
    @PatchMapping("/items/{productId}")
    public ResponseEntity<Cart> updateItemQuantity(
            @RequestParam(required = false) String projectId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        Cart cart = cartService.updateItemQuantity(
                userId,
                projectId,
                productId,
                request.getQuantity()
        );

        return ResponseEntity.ok(cart);
    }
}
