package com.example.ecommerce.catalog.repository;

import com.example.ecommerce.catalog.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByName(String name);

    boolean existsByIdAndProductsIsNotEmpty(UUID id);
}
