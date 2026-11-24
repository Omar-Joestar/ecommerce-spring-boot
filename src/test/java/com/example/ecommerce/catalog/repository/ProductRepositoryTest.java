
package com.example.ecommerce.catalog.repository;

import com.example.ecommerce.catalog.entity.CategoryEntity;
import com.example.ecommerce.catalog.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("ecommerce_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveProductSuccessfully() {
        CategoryEntity category = saveCategory("Electronics");

        ProductEntity product = createProduct(
                "Laptop", "LAP-001", category, true
        );

        ProductEntity saved = productRepository.save(product);

        assertNotNull(saved.getId());
        assertEquals("Laptop", saved.getName());
        assertEquals(category.getId(), saved.getCategory().getId());
    }

    @Test
    void existsBySkuReturnsTrue() {
        CategoryEntity category = saveCategory("Electronics");

        productRepository.save(
                createProduct("Laptop", "LAP-001", category, true)
        );

        assertTrue(productRepository.existsBySku("LAP-001"));
    }

    @Test
    void existsBySkuReturnsFalse() {
        assertFalse(productRepository.existsBySku("UNKNOWN"));
    }

    @Test
    void findByActiveTrueReturnsOnlyActiveProducts() {
        CategoryEntity category = saveCategory("Electronics");

        productRepository.save(
                createProduct("Laptop", "LAP-001", category, true)
        );
        productRepository.save(
                createProduct("Phone", "PHN-001", category, false)
        );

        List<ProductEntity> products = productRepository.findByActiveTrue();

        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }

    @Test
    void findByIdAndActiveTrueReturnsActiveProduct() {
        CategoryEntity category = saveCategory("Electronics");

        ProductEntity saved = productRepository.save(
                createProduct("Laptop", "LAP-001", category, true)
        );

        assertTrue(
                productRepository.findByIdAndActiveTrue(saved.getId()).isPresent()
        );
    }

    @Test
    void findByIdAndActiveTrueReturnsEmptyForInactiveProduct() {
        CategoryEntity category = saveCategory("Electronics");

        ProductEntity saved = productRepository.save(
                createProduct("Laptop", "LAP-001", category, false)
        );

        assertTrue(
                productRepository.findByIdAndActiveTrue(saved.getId()).isEmpty()
        );
    }

    @Test
    void findByCategoryIdAndActiveTrueReturnsOnlyActiveProducts() {
        CategoryEntity category = saveCategory("Electronics");

        productRepository.save(
                createProduct("Laptop", "LAP-001", category, true)
        );
        productRepository.save(
                createProduct("Phone", "PHN-001", category, false)
        );

        List<ProductEntity> products =
                productRepository.findByCategoryIdAndActiveTrue(category.getId());

        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }

    @Test
    void findByNameContainingIgnoreCaseAndActiveTrueFindsMatchingProducts() {
        CategoryEntity category = saveCategory("Electronics");

        productRepository.save(
                createProduct("Gaming Laptop", "LAP-001", category, true)
        );
        productRepository.save(
                createProduct("Gaming Mouse", "MOU-001", category, false)
        );

        List<ProductEntity> products =
                productRepository.findByNameContainingIgnoreCaseAndActiveTrue("gaming");

        assertEquals(1, products.size());
        assertEquals("Gaming Laptop", products.get(0).getName());
    }

    private CategoryEntity saveCategory(String name) {
        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setDescription(name + " category");
        return categoryRepository.save(category);
    }

    private ProductEntity createProduct(
            String name,
            String sku,
            CategoryEntity category,
            boolean active
    ) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setDescription(name + " description");
        product.setSku(sku);
        product.setPrice(new BigDecimal("100.00"));
        product.setCategory(category);
        product.setActive(active);
        return product;
    }
}
