package com.example.ecommerce.catalog.repository;

import com.example.ecommerce.catalog.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    boolean existsBySku(String sku);
    List<ProductEntity> findByCategoryId(UUID categoryId);
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    List<ProductEntity> findByActiveTrue();
    List<ProductEntity> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    List<ProductEntity> findByCategoryIdAndActiveTrue(UUID categoryId);
    Optional<ProductEntity> findByIdAndActiveTrue(UUID id);
}
