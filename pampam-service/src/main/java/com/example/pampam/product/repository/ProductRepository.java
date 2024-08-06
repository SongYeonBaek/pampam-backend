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

}
