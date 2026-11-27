
package com.example.ecommerce.inventory.controller;

import com.example.ecommerce.inventory.dto.InventoryDTO;
import com.example.ecommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public InventoryDTO createInventory(@Valid @RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.createInventory(
                inventoryDTO.getProductId(),
                inventoryDTO.getQuantity()
        );
    }

    @GetMapping
    public List<InventoryDTO> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/product/{productId}")
    public InventoryDTO getInventoryByProductId(
            @PathVariable UUID productId
    ) {
        return inventoryService.getInventoryByProductId(productId);
    }


    @PatchMapping("/product/{productId}/add")
    public InventoryDTO addStock(
            @PathVariable UUID productId,
            @RequestParam @jakarta.validation.constraints.Positive Integer quantity
    ) {
        return inventoryService.addStock(productId, quantity);
    }

    @PatchMapping("/product/{productId}/remove")
    public InventoryDTO removeStock(
            @PathVariable UUID productId,
            @RequestParam @jakarta.validation.constraints.Positive Integer quantity
    ) {
        return inventoryService.removeStock(productId, quantity);
    }

}
