
package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.CartDTO;
import com.example.ecommerce.cart.dto.CartItemDTO;
import com.example.ecommerce.cart.entity.CartEntity;
import com.example.ecommerce.cart.entity.CartItemEntity;
import com.example.ecommerce.cart.mapper.CartMapper;
import com.example.ecommerce.cart.repository.CartItemRepository;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.catalog.service.ProductService;
import com.example.ecommerce.user.entity.UserEntity;
import com.example.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public CartDTO createCart(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (cartRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("User already has a cart");
        }

        UserEntity user = userService.getUserById(userId);

        CartEntity cart = new CartEntity();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        CartEntity savedCart = cartRepository.save(cart);

        return cartMapper.toDTO(savedCart);
    }

    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Long userId) {

        CartEntity cart = getCartEntityByUserId(userId);

        return cartMapper.toDTO(cart);
    }

    @Transactional
    public CartDTO addItem(Long userId, CartItemDTO itemDTO) {

        if (itemDTO == null || itemDTO.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }

        validateQuantity(itemDTO.getQuantity());

        CartEntity cart = getCartEntityByUserId(userId);

        // Ensure the product exists and is active.
        productService.getProductById(itemDTO.getProductId());

        CartItemEntity existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), itemDTO.getProductId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(
                    existingItem.getQuantity() + itemDTO.getQuantity()
            );
            cartItemRepository.save(existingItem);
        } else {
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart);
            newItem.setProductId(itemDTO.getProductId());
            newItem.setQuantity(itemDTO.getQuantity());

            cartItemRepository.save(newItem);
        }

        return cartMapper.toDTO(getCartEntityByUserId(userId));
    }

    @Transactional
    public CartDTO updateItemQuantity(
            Long userId,
            UUID productId,
            Integer quantity
    ) {

        validateQuantity(quantity);

        CartEntity cart = getCartEntityByUserId(userId);

        CartItemEntity item = getCartItem(cart.getId(), productId);

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return cartMapper.toDTO(getCartEntityByUserId(userId));
    }

    @Transactional
    public CartDTO removeItem(Long userId, UUID productId) {

        CartEntity cart = getCartEntityByUserId(userId);

        CartItemEntity item = getCartItem(cart.getId(), productId);

        cartItemRepository.delete(item);

        return cartMapper.toDTO(getCartEntityByUserId(userId));
    }

    @Transactional
    public void clearCart(Long userId) {

        CartEntity cart = getCartEntityByUserId(userId);

        cartItemRepository.deleteByCartId(cart.getId());
    }

    private CartEntity getCartEntityByUserId(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Cart not found for user: " + userId
                ));
    }

    private CartItemEntity getCartItem(UUID cartId, UUID productId) {

        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }

        return cartItemRepository
                .findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found in cart"
                ));
    }

    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }
}
