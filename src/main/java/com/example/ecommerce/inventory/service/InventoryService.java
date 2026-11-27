
package com.example.ecommerce.inventory.service;

import com.example.ecommerce.catalog.service.ProductService;
import com.example.ecommerce.inventory.dto.InventoryDTO;
import com.example.ecommerce.inventory.entity.InventoryEntity;
import com.example.ecommerce.inventory.mapper.InventoryMapper;
import com.example.ecommerce.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductService productService;

    @Transactional
    public InventoryDTO createInventory(UUID productId, Integer quantity) {

        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }

        productService.getProductById(productId);

        if (inventoryRepository.existsByProductId(productId)) {
            throw new IllegalArgumentException(
                    "Inventory already exists for this product"
            );
        }

        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be negative or null"
            );
        }

        InventoryEntity inventory = new InventoryEntity();
        inventory.setProductId(productId);
        inventory.setQuantity(quantity);

        InventoryEntity savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toDTO(savedInventory);
    }

    public InventoryDTO getInventoryByProductId(UUID productId) {

        InventoryEntity inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException(
                        "Inventory not found for product: " + productId
                ));

        return inventoryMapper.toDTO(inventory);
    }

    public List<InventoryDTO> getAllInventory() {
        return inventoryMapper.toDTOList(inventoryRepository.findAll());
    }

    @Transactional
    public InventoryDTO addStock(UUID productId, Integer quantity) {

        validateQuantity(quantity);

        InventoryEntity inventory = getInventoryEntity(productId);

        inventory.setQuantity(inventory.getQuantity() + quantity);

        try {
            InventoryEntity savedInventory = inventoryRepository.save(inventory);
            return inventoryMapper.toDTO(savedInventory);
        } catch (OptimisticLockingFailureException ex) {
            throw new IllegalStateException(
                    "Inventory was modified by another request. Please try again."
            );
        }
    }

    @Transactional
    public InventoryDTO removeStock(UUID productId, Integer quantity) {

        validateQuantity(quantity);

        InventoryEntity inventory = getInventoryEntity(productId);

        if (inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);

        try {
            InventoryEntity savedInventory = inventoryRepository.save(inventory);
            return inventoryMapper.toDTO(savedInventory);
        } catch (OptimisticLockingFailureException ex) {
            throw new IllegalStateException(
                    "Inventory was modified by another request. Please try again."
            );
        }
    }

    private InventoryEntity getInventoryEntity(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException(
                        "Inventory not found for product: " + productId
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
