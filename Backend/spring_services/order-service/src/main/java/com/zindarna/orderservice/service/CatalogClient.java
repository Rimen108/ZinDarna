
package com.zindarna.orderservice.service;

import com.zindarna.orderservice.dto.CatalogProductResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(
            @LoadBalanced RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder
                .baseUrl("http://catalog-service")
                .build();
    }

    public CatalogProductResponse getProductById(String productId) {

        return restClient.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .body(CatalogProductResponse.class);
    }
}
