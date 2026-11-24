package com.example.ecommerce.catalog.mapper;

import com.example.ecommerce.catalog.dto.CategoryDTO;
import com.example.ecommerce.catalog.entity.CategoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO(CategoryEntity category);

    CategoryEntity toEntity(CategoryDTO categoryDTO);

    List<CategoryDTO> toDTOList(List<CategoryEntity> categories);
}