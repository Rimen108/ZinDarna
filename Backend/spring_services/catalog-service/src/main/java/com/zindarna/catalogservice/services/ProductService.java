
package com.zindarna.catalogservice.service;

import com.zindarna.catalogservice.client.ProfileClient;
import com.zindarna.catalogservice.client.ProfileClient.ProfileResponse;
import com.zindarna.catalogservice.exception.ResourceNotFoundException;
import com.zindarna.catalogservice.model.Product;
import com.zindarna.catalogservice.repository.ProductRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProfileClient profileClient;

    public ProductService(
            ProductRepository productRepository,
            ProfileClient profileClient) {
        this.productRepository = productRepository;
        this.profileClient = profileClient;
    }

    // Récupérer et vérifier le profil du supplier connecté
    private ProfileResponse getApprovedSupplier(Jwt jwt) {

        ProfileResponse profile =
                profileClient.getMyProfile(jwt.getTokenValue());

        if (profile == null || profile.getId() == null) {
            throw new AccessDeniedException(
                    "Supplier profile not found"
            );
        }

        if (!"SUPPLIER".equals(profile.getAccountType())) {
            throw new AccessDeniedException(
                    "Only suppliers can manage products"
            );
        }

        if (!"APPROVED".equals(profile.getSupplierStatus())) {
            throw new AccessDeniedException(
                    "Supplier account is not approved"
            );
        }

        return profile;
    }

    // CREATE : Ajouter un produit
    public Product createProduct(Product product, Jwt jwt) {

        ProfileResponse profile = getApprovedSupplier(jwt);

        // L'identifiant vient du profil de Sara,
        // jamais du frontend
        product.setSupplierId(profile.getId());

        return productRepository.save(product);
    }

    // READ : Consulter tous les produits (public)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ : Consulter uniquement ses propres produits
    public List<Product> getMyProducts(Jwt jwt) {

        ProfileResponse profile = getApprovedSupplier(jwt);

        return productRepository.findBySupplierId(profile.getId());
    }

    // READ : Consulter un produit par son ID (public)
    public Product getProductById(String id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));
    }

    // UPDATE : Modifier uniquement ses propres produits
    public Product updateProduct(
            String id,
            Product product,
            Jwt jwt) {

        ProfileResponse profile = getApprovedSupplier(jwt);

        Product existingProduct =
                productRepository.findByIdAndSupplierId(
                        id,
                        profile.getId()
                ).orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found or does not belong to you"
                ));

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategoryId(product.getCategoryId());
        existingProduct.setImageUrls(product.getImageUrls());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setMaterial(product.getMaterial());
        existingProduct.setColor(product.getColor());
        existingProduct.setStyle(product.getStyle());
        existingProduct.setAvailable(product.isAvailable());

        // Ne jamais modifier supplierId pendant un update
        existingProduct.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(existingProduct);
    }

    // DELETE : Supprimer uniquement ses propres produits
    public void deleteProduct(String id, Jwt jwt) {

        ProfileResponse profile = getApprovedSupplier(jwt);

        Product product =
                productRepository.findByIdAndSupplierId(
                        id,
                        profile.getId()
                ).orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found or does not belong to you"
                ));

        productRepository.delete(product);
    }
}
