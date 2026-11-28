
package com.example.ecommerce.cart.controller;

import com.example.ecommerce.cart.dto.CartDTO;
import com.example.ecommerce.cart.dto.CartItemDTO;
import com.example.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}")
    public CartDTO createCart(@PathVariable Long userId) {
        return cartService.createCart(userId);
    }

    @GetMapping("/{userId}")
    public CartDTO getCart(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId);
    }

    @PostMapping("/{userId}/items")
    public CartDTO addItem(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemDTO itemDTO
    ) {
        return cartService.addItem(userId, itemDTO);
    }

    @PutMapping("/{userId}/items/{productId}")
    public CartDTO updateItemQuantity(
            @PathVariable Long userId,
            @PathVariable UUID productId,
            @RequestParam @jakarta.validation.constraints.Positive Integer quantity
    ) {
        return cartService.updateItemQuantity(userId, productId, quantity);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public CartDTO removeItem(
            @PathVariable Long userId,
            @PathVariable UUID productId
    ) {
        return cartService.removeItem(userId, productId);
    }

    @DeleteMapping("/{userId}/items")
    public void clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
    }
}
