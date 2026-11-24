package com.example.ecommerce.catalog.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private UUID categoryId;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}