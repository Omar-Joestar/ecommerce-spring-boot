
package com.example.ecommerce.catalog.repository;

import com.example.ecommerce.catalog.entity.CategoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("ecommerce_test")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveCategorySuccessfully() {
        CategoryEntity category = createCategory("Electronics");

        CategoryEntity saved = categoryRepository.save(category);

        assertNotNull(saved.getId());
        assertEquals("Electronics", saved.getName());
    }

    @Test
    void findCategoryByIdSuccessfully() {
        CategoryEntity saved =
                categoryRepository.save(createCategory("Electronics"));

        CategoryEntity found = categoryRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(saved.getId(), found.getId());
        assertEquals("Electronics", found.getName());
    }

    @Test
    void findAllCategoriesSuccessfully() {
        categoryRepository.save(createCategory("Electronics"));
        categoryRepository.save(createCategory("Books"));

        assertEquals(2, categoryRepository.findAll().size());
    }

    @Test
    void existsByNameReturnsTrue() {
        categoryRepository.save(createCategory("Electronics"));

        assertTrue(categoryRepository.existsByName("Electronics"));
    }

    @Test
    void existsByNameReturnsFalse() {
        assertFalse(categoryRepository.existsByName("Unknown"));
    }

    @Test
    void existsByIdAndProductsIsNotEmptyReturnsFalseForEmptyCategory() {
        CategoryEntity saved =
                categoryRepository.save(createCategory("Electronics"));

        assertFalse(
                categoryRepository.existsByIdAndProductsIsNotEmpty(saved.getId())
        );
    }

    @Test
    void deleteCategorySuccessfully() {
        CategoryEntity saved =
                categoryRepository.save(createCategory("Electronics"));

        categoryRepository.delete(saved);

        assertFalse(categoryRepository.existsById(saved.getId()));
    }

    private CategoryEntity createCategory(String name) {
        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setDescription(name + " category");
        return category;
    }
}
