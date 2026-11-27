package com.example.ecommerce.inventory.mapper;

import com.example.ecommerce.inventory.dto.InventoryDTO;
import com.example.ecommerce.inventory.entity.InventoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryDTO toDTO(InventoryEntity inventory);

    InventoryEntity toEntity(InventoryDTO inventoryDTO);

    List<InventoryDTO> toDTOList(List<InventoryEntity> inventories);
}
