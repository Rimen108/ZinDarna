
package com.zindarna.catalogservice.repository;

import com.zindarna.catalogservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
        extends MongoRepository<Product, String> {

    // Récupérer uniquement les produits d'un supplier
    List<Product> findBySupplierId(String supplierId);

    // Récupérer un produit appartenant à un supplier précis
    Optional<Product> findByIdAndSupplierId(
            String id,
            String supplierId
    );
}
