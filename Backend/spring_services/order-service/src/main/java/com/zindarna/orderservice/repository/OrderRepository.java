package com.zindarna.orderservice.repository;

import com.zindarna.orderservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByUserId(String userId);

    List<Order> findByOrderStatus(String orderStatus);

    List<Order> findByUserIdAndOrderStatus(
            String userId,
            String orderStatus
    );

    List<Order> findBySupplierOrdersSupplierId(String supplierId);

    Order findByIdAndUserId(String id, String userId);
    Optional<Order> findByCheckoutKeyAndUserId(
            String checkoutKey,
            String userId
    );
}