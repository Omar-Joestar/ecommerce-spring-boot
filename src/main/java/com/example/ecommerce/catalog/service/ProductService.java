package com.example.ecommerce.catalog.service;

import com.example.ecommerce.catalog.dto.ProductDTO;
import com.example.ecommerce.catalog.entity.CategoryEntity;
import com.example.ecommerce.catalog.entity.ProductEntity;
import com.example.ecommerce.catalog.mapper.ProductMapper;
import com.example.ecommerce.catalog.repository.CategoryRepository;
import com.example.ecommerce.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public List<ProductDTO> getAllProducts() {
        return productMapper.toDTOList(productRepository.findByActiveTrue());
    }

    public ProductDTO getProductById(UUID id) {
        ProductEntity product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        return productMapper.toDTO(product);
    }

    private void checkSkuAvailability(String sku) {
        if (productRepository.existsBySku(sku)) {
            throw new IllegalArgumentException("SKU already exists: " + sku);
        }
    }

    public ProductDTO createProduct(ProductDTO productDTO) {

        checkSkuAvailability(productDTO.getSku());

        CategoryEntity category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ProductEntity product = productMapper.toEntity(productDTO);

        product.setId(null);
        product.setCategory(category);
        product.setActive(true);

        ProductEntity savedProduct = productRepository.save(product);

        return productMapper.toDTO(savedProduct);
    }

    public ProductDTO updateProduct(UUID id, ProductDTO productDTO) {

        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (!existingProduct.getSku().equalsIgnoreCase(productDTO.getSku())) {
            checkSkuAvailability(productDTO.getSku());
        }

        CategoryEntity category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setSku(productDTO.getSku());
        existingProduct.setCategory(category);
        existingProduct.setActive(productDTO.getActive());

        ProductEntity updatedProduct = productRepository.save(existingProduct);

        return productMapper.toDTO(updatedProduct);
    }

    public List<ProductDTO> getProductsByCategory(UUID categoryId) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }

        List<ProductEntity> products =
                productRepository.findByCategoryIdAndActiveTrue(categoryId);
        return productMapper.toDTOList(products);
    }

    public List<ProductDTO> getProductsByName(String name) {
        List<ProductEntity> products = productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
        return productMapper.toDTOList(products);

    }

    public ProductDTO deactivateProduct(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setActive(false);

        ProductEntity savedProduct = productRepository.save(product);

        return productMapper.toDTO(savedProduct);
    }

    public ProductDTO activateProduct(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setActive(true);

        ProductEntity savedProduct = productRepository.save(product);

        return productMapper.toDTO(savedProduct);
    }
}

