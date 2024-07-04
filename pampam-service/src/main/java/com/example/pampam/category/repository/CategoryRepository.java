package com.example.pampam.category.repository;

import com.example.pampam.category.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByCategoryType(String categoryType);
    Optional<Category> findByCategoryType(String categoryType);
}
