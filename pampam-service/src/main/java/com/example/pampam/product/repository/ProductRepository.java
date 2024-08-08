package com.example.pampam.product.repository;

import com.example.pampam.product.model.entity.Product;
import com.example.pampam.product.repository.querydsl.ProductRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Optional<Product> findByproductName(String name);

    List<Product> findByProductNameContaining(String keyword);

    List<Product> findAllByCloseAt(Date closeAt);


    // available이 true인 상품을 마감시간(closeAt)을 기준으로 내림차순 정렬, 결과를 페이지 크기로 제한
    List<Product> findByAvailableTrueOrderByCloseAtDesc(Pageable pageable);

    // available이 true인 상품을 공동구매 참여 인원(peopleCount)을 기준으로 내림차순 정렬, 결과를 페이지 크기로 제한
    List<Product> findByAvailableTrueOrderByPeopleCountDesc(Pageable pageable);
}
