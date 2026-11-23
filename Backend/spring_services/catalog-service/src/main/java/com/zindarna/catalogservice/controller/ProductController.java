
package com.zindarna.catalogservice.controller;

import com.zindarna.catalogservice.model.Product;
import com.zindarna.catalogservice.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Ajouter un produit : fournisseur authentifié
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                productService.createProduct(product, jwt)
        );
    }

    // Consulter tous les produits : public
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Consulter un produit : public
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // Consulter les produits du fournisseur connecté
    @GetMapping("/my-products")
    public ResponseEntity<List<Product>> getMyProducts(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(productService.getMyProducts(jwt));
    }

    // Modifier un produit appartenant au fournisseur connecté
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @RequestBody Product product,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                productService.updateProduct(id, product, jwt)
        );
    }

    // Supprimer un produit appartenant au fournisseur connecté
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

        productService.deleteProduct(id, jwt);
        return ResponseEntity.noContent().build();
    }
}
