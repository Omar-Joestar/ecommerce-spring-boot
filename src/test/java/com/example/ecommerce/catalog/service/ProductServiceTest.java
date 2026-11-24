
package com.example.ecommerce.catalog.service;

import com.example.ecommerce.catalog.dto.ProductDTO;
import com.example.ecommerce.catalog.entity.CategoryEntity;
import com.example.ecommerce.catalog.entity.ProductEntity;
import com.example.ecommerce.catalog.mapper.ProductMapper;
import com.example.ecommerce.catalog.repository.CategoryRepository;
import com.example.ecommerce.catalog.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProductsSuccessfully() {
        List<ProductEntity> entities = List.of(new ProductEntity());
        List<ProductDTO> expected = List.of(new ProductDTO());

        when(productRepository.findByActiveTrue()).thenReturn(entities);
        when(productMapper.toDTOList(entities)).thenReturn(expected);

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(expected, result);
        verify(productRepository).findByActiveTrue();
    }

    @Test
    void getProductByIdSuccessfully() {
        UUID id = UUID.randomUUID();
        ProductEntity entity = new ProductEntity();
        ProductDTO expected = new ProductDTO();

        when(productRepository.findByIdAndActiveTrue(id))
                .thenReturn(Optional.of(entity));
        when(productMapper.toDTO(entity)).thenReturn(expected);

        ProductDTO result = productService.getProductById(id);

        assertEquals(expected, result);
    }

    @Test
    void getProductByIdThrowsExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findByIdAndActiveTrue(id))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> productService.getProductById(id)
        );
    }

    @Test
    void createProductSuccessfully() {
        UUID categoryId = UUID.randomUUID();

        ProductDTO dto = new ProductDTO();
        dto.setName("Laptop");
        dto.setSku("LAP-001");
        dto.setPrice(new BigDecimal("1200.00"));
        dto.setCategoryId(categoryId);

        CategoryEntity category = new CategoryEntity();

        ProductEntity product = new ProductEntity();
        ProductEntity savedProduct = new ProductEntity();
        ProductDTO expected = new ProductDTO();

        when(productRepository.existsBySku("LAP-001")).thenReturn(false);
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));
        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(expected);

        ProductDTO result = productService.createProduct(dto);

        assertEquals(expected, result);
        assertNull(product.getId());
        assertSame(category, product.getCategory());
        assertEquals(Boolean.TRUE, product.getActive());

        verify(productRepository).save(product);
    }

    @Test
    void createProductWithExistingSkuThrowsException() {
        ProductDTO dto = new ProductDTO();
        dto.setSku("LAP-001");

        when(productRepository.existsBySku("LAP-001")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(dto)
        );

        verify(categoryRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProductWithUnknownCategoryThrowsException() {
        UUID categoryId = UUID.randomUUID();

        ProductDTO dto = new ProductDTO();
        dto.setSku("LAP-001");
        dto.setCategoryId(categoryId);

        when(productRepository.existsBySku("LAP-001")).thenReturn(false);
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> productService.createProduct(dto)
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductDTO dto = new ProductDTO();
        dto.setName("Updated Laptop");
        dto.setDescription("Updated description");
        dto.setPrice(new BigDecimal("1500.00"));
        dto.setSku("LAP-001");
        dto.setCategoryId(categoryId);
        dto.setActive(true);

        ProductEntity existing = new ProductEntity();
        existing.setName("Laptop");
        existing.setSku("LAP-001");

        CategoryEntity category = new CategoryEntity();
        ProductDTO expected = new ProductDTO();

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));
        when(productRepository.save(existing)).thenReturn(existing);
        when(productMapper.toDTO(existing)).thenReturn(expected);

        ProductDTO result = productService.updateProduct(id, dto);

        assertEquals("Updated Laptop", existing.getName());
        assertEquals("Updated description", existing.getDescription());
        assertEquals(new BigDecimal("1500.00"), existing.getPrice());
        assertEquals("LAP-001", existing.getSku());
        assertSame(category, existing.getCategory());
        assertEquals(Boolean.TRUE, existing.getActive());
        assertEquals(expected, result);

        verify(productRepository).save(existing);
    }

    @Test
    void updateProductWithDuplicateSkuThrowsException() {
        UUID id = UUID.randomUUID();

        ProductDTO dto = new ProductDTO();
        dto.setSku("NEW-SKU");

        ProductEntity existing = new ProductEntity();
        existing.setSku("OLD-SKU");

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySku("NEW-SKU")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(id, dto)
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductWithUnknownCategoryThrowsException() {
        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductDTO dto = new ProductDTO();
        dto.setSku("OLD-SKU");
        dto.setCategoryId(categoryId);

        ProductEntity existing = new ProductEntity();
        existing.setSku("OLD-SKU");

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> productService.updateProduct(id, dto)
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductThrowsExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> productService.updateProduct(id, new ProductDTO())
        );
    }

    @Test
    void getProductsByCategorySuccessfully() {
        UUID categoryId = UUID.randomUUID();

        List<ProductEntity> entities = List.of(new ProductEntity());
        List<ProductDTO> expected = List.of(new ProductDTO());

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(productRepository.findByCategoryIdAndActiveTrue(categoryId))
                .thenReturn(entities);
        when(productMapper.toDTOList(entities)).thenReturn(expected);

        List<ProductDTO> result = productService.getProductsByCategory(categoryId);

        assertEquals(expected, result);
    }

    @Test
    void getProductsByCategoryThrowsExceptionWhenCategoryNotFound() {
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> productService.getProductsByCategory(categoryId)
        );

        verify(productRepository, never())
                .findByCategoryIdAndActiveTrue(any());
    }

    @Test
    void getProductsByNameSuccessfully() {
        String name = "Laptop";

        List<ProductEntity> entities = List.of(new ProductEntity());
        List<ProductDTO> expected = List.of(new ProductDTO());

        when(productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name))
                .thenReturn(entities);
        when(productMapper.toDTOList(entities)).thenReturn(expected);

        List<ProductDTO> result = productService.getProductsByName(name);

        assertEquals(expected, result);
    }

    @Test
    void deactivateProductSuccessfully() {
        UUID id = UUID.randomUUID();

        ProductEntity product = new ProductEntity();
        product.setActive(true);

        ProductDTO expected = new ProductDTO();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(expected);

        ProductDTO result = productService.deactivateProduct(id);

        assertEquals(Boolean.FALSE, product.getActive());
        assertEquals(expected, result);

        verify(productRepository).save(product);
    }

    @Test
    void activateProductSuccessfully() {
        UUID id = UUID.randomUUID();

        ProductEntity product = new ProductEntity();
        product.setActive(false);

        ProductDTO expected = new ProductDTO();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(expected);

        ProductDTO result = productService.activateProduct(id);

        assertEquals(Boolean.TRUE, product.getActive());
        assertEquals(expected, result);

        verify(productRepository).save(product);
    }

    @Test
    void deactivateProductThrowsExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> productService.deactivateProduct(id)
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void activateProductThrowsExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> productService.activateProduct(id)
        );

        verify(productRepository, never()).save(any());
    }
}
