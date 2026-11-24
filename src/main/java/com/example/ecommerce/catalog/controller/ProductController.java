package com.example.ecommerce.catalog.controller;

import com.example.ecommerce.catalog.dto.ProductDTO;
import com.example.ecommerce.catalog.service.CategoryService;
import com.example.ecommerce.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping
    public List<ProductDTO> getProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable UUID id){
        return productService.getProductById(id);

    }
    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO){
        return productService.createProduct(productDTO);
    }

    @PutMapping("/{id}")
    public ProductDTO updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }


    @GetMapping("/category/{categoryId}")
    public List<ProductDTO> getProductsByCategory(
            @PathVariable UUID categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping("/search")
    public List<ProductDTO> getProductsByName(@RequestParam String name) {
        return productService.getProductsByName(name);
    }

    @PatchMapping("/{id}/deactivate")
    public ProductDTO deactivateProduct(@PathVariable UUID id) {
        return productService.deactivateProduct(id);
    }

    @PatchMapping("/{id}/activate")
    public ProductDTO activateProduct(@PathVariable UUID id) {
        return productService.activateProduct(id);
    }


}
