package com.example.pampam.product.repository;

import com.example.pampam.product.model.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    Optional<ProductImage> findProductImagesByIdx(Long idx);
    List<ProductImage> findByProductIdx(Long productIdx);
}
