package com.example.pampam.orders.repository;

import com.example.pampam.orders.model.entity.OrderedProduct;
import com.example.pampam.orders.model.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Integer> {
    List<OrderedProduct> findAllByConsumerIdx(Long consumerIdx);
}
