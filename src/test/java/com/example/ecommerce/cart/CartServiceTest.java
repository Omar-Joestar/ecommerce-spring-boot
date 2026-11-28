
package com.example.ecommerce.cart;

import com.example.ecommerce.cart.dto.CartDTO;
import com.example.ecommerce.cart.dto.CartItemDTO;
import com.example.ecommerce.cart.entity.CartEntity;
import com.example.ecommerce.cart.entity.CartItemEntity;
import com.example.ecommerce.cart.mapper.CartMapper;
import com.example.ecommerce.cart.repository.CartItemRepository;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.catalog.service.ProductService;
import com.example.ecommerce.user.entity.UserEntity;
import com.example.ecommerce.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private Long userId;
    private UUID cartId;
    private UUID productId;

    private UserEntity user;
    private CartEntity cart;
    private CartItemEntity cartItem;
    private CartDTO cartDTO;

    @BeforeEach
    void setUp() {
        userId = 1L;
        cartId = UUID.randomUUID();
        productId = UUID.randomUUID();

        user = new UserEntity();
        user.setId(userId);

        cart = new CartEntity();
        cart.setId(cartId);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        cartItem = new CartItemEntity();
        cartItem.setId(UUID.randomUUID());
        cartItem.setCart(cart);
        cartItem.setProductId(productId);
        cartItem.setQuantity(2);

        cartDTO = new CartDTO();
        cartDTO.setId(cartId);
        cartDTO.setUserId(userId);
        cartDTO.setItems(new ArrayList<>());
    }

    @Test
    void createCart_shouldCreateCartSuccessfully() {
        when(cartRepository.existsByUserId(userId)).thenReturn(false);
        when(userService.getUserById(userId)).thenReturn(user);
        when(cartRepository.save(any(CartEntity.class))).thenReturn(cart);
        when(cartMapper.toDTO(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.createCart(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());

        verify(userService).getUserById(userId);
        verify(cartRepository).save(any(CartEntity.class));
    }

    @Test
    void createCart_shouldRejectDuplicateCart() {
        when(cartRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.createCart(userId)
        );

        verify(cartRepository, never()).save(any());
    }

    @Test
    void createCart_shouldRejectNullUserId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.createCart(null)
        );

        verifyNoInteractions(cartRepository);
    }

    @Test
    void getCartByUserId_shouldReturnCart() {
        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(cartMapper.toDTO(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.getCartByUserId(userId);

        assertNotNull(result);
        assertEquals(cartId, result.getId());
    }

    @Test
    void getCartByUserId_shouldThrowWhenCartNotFound() {
        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> cartService.getCartByUserId(userId)
        );
    }

    @Test
    void addItem_shouldAddNewProduct() {
        CartItemDTO itemDTO = new CartItemDTO();
        itemDTO.setProductId(productId);
        itemDTO.setQuantity(2);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.empty());
        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(cartMapper.toDTO(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.addItem(userId, itemDTO);

        assertNotNull(result);

        verify(productService).getProductById(productId);
        verify(cartItemRepository).save(any(CartItemEntity.class));
    }

    @Test
    void addItem_shouldIncreaseQuantityWhenProductAlreadyExists() {
        CartItemDTO itemDTO = new CartItemDTO();
        itemDTO.setProductId(productId);
        itemDTO.setQuantity(3);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.of(cartItem));
        when(cartMapper.toDTO(cart)).thenReturn(cartDTO);

        cartService.addItem(userId, itemDTO);

        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void addItem_shouldRejectInvalidQuantity() {
        CartItemDTO itemDTO = new CartItemDTO();
        itemDTO.setProductId(productId);
        itemDTO.setQuantity(0);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addItem(userId, itemDTO)
        );

        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void updateItemQuantity_shouldUpdateSuccessfully() {
        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.of(cartItem));
        when(cartMapper.toDTO(cart)).thenReturn(cartDTO);

        cartService.updateItemQuantity(userId, productId, 5);

        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void removeItem_shouldDeleteProduct() {
        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.of(cartItem));
        when(cartMapper.toDTO(cart)).thenReturn(cartDTO);

        cartService.removeItem(userId, productId);

        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void clearCart_shouldDeleteAllItems() {
        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        cartService.clearCart(userId);

        verify(cartItemRepository).deleteByCartId(cartId);
    }
}
