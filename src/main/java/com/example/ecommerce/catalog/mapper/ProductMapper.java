package com.example.ecommerce.catalog.mapper;

import com.example.ecommerce.catalog.dto.ProductDTO;
import com.example.ecommerce.catalog.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    ProductDTO toDTO(ProductEntity product);

    @Mapping(source = "categoryId", target = "category.id")
    ProductEntity toEntity(ProductDTO productDTO);

    List<ProductDTO> toDTOList(List<ProductEntity> products);
}
