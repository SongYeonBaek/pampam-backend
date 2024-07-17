package com.example.pampam.cart.repository;

import com.example.pampam.cart.model.entity.Cart;
import com.example.pampam.member.model.entity.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByConsumerIdx(Long consumerIdx);

    void deleteAllByConsumerIdx(Long consumerIdx);

    List<Cart> findAllByConsumerIdxAndProductIdx(Long consumerIdx, Long productIdx);
}
