
package com.example.ecommerce.cart.mapper;

import com.example.ecommerce.cart.dto.CartDTO;
import com.example.ecommerce.cart.dto.CartItemDTO;
import com.example.ecommerce.cart.entity.CartEntity;
import com.example.ecommerce.cart.entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "user.id")
    CartDTO toDTO(CartEntity cart);

    @Mapping(target = "user", ignore = true)
    CartEntity toEntity(CartDTO cartDTO);

    CartItemDTO toDTO(CartItemEntity cartItem);

    @Mapping(target = "cart", ignore = true)
    CartItemEntity toEntity(CartItemDTO cartItemDTO);

    List<CartItemDTO> toItemDTOList(List<CartItemEntity> items);
}
