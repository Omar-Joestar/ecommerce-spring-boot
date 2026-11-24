package com.example.ecommerce.user.mapper;

import com.example.ecommerce.user.dto.UserDTO;
import com.example.ecommerce.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(UserEntity user);
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    UserEntity toEntity(UserDTO dto);
}