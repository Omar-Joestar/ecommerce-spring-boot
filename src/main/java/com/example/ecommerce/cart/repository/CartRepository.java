
package com.example.ecommerce.cart.repository;

import com.example.ecommerce.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {

    Optional<CartEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
