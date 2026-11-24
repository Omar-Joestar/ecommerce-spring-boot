
package com.example.ecommerce.catalog.service;

import com.example.ecommerce.catalog.dto.CategoryDTO;
import com.example.ecommerce.catalog.entity.CategoryEntity;
import com.example.ecommerce.catalog.mapper.CategoryMapper;
import com.example.ecommerce.catalog.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategoriesSuccessfully() {
        List<CategoryEntity> entities = List.of(new CategoryEntity());
        List<CategoryDTO> expected = List.of(new CategoryDTO());

        when(categoryRepository.findAll()).thenReturn(entities);
        when(categoryMapper.toDTOList(entities)).thenReturn(expected);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(expected, result);
        verify(categoryRepository).findAll();
        verify(categoryMapper).toDTOList(entities);
    }

    @Test
    void addCategorySuccessfully() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Electronics");

        CategoryEntity entity = new CategoryEntity();
        entity.setName("Electronics");

        CategoryEntity savedEntity = new CategoryEntity();
        savedEntity.setName("Electronics");

        CategoryDTO expected = new CategoryDTO();
        expected.setName("Electronics");

        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryMapper.toEntity(dto)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(savedEntity);
        when(categoryMapper.toDTO(savedEntity)).thenReturn(expected);

        CategoryDTO result = categoryService.addCategory(dto);

        assertEquals(expected, result);
        verify(categoryRepository).existsByName("Electronics");
        verify(categoryRepository).save(entity);
    }

    @Test
    void addCategoryWithExistingNameThrowsException() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Electronics");

        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.addCategory(dto)
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getCategoryByIdSuccessfully() {
        UUID id = UUID.randomUUID();
        CategoryEntity entity = new CategoryEntity();
        CategoryDTO expected = new CategoryDTO();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(categoryMapper.toDTO(entity)).thenReturn(expected);

        CategoryDTO result = categoryService.getCategoryById(id);

        assertEquals(expected, result);
    }

    @Test
    void getCategoryByIdThrowsExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> categoryService.getCategoryById(id)
        );
    }

    @Test
    void updateCategorySuccessfully() {
        UUID id = UUID.randomUUID();

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Updated Electronics");
        dto.setDescription("Updated description");

        CategoryEntity existing = new CategoryEntity();
        existing.setName("Electronics");
        existing.setDescription("Old description");

        CategoryDTO expected = new CategoryDTO();
        expected.setName("Updated Electronics");
        expected.setDescription("Updated description");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("Updated Electronics")).thenReturn(false);
        when(categoryRepository.save(existing)).thenReturn(existing);
        when(categoryMapper.toDTO(existing)).thenReturn(expected);

        CategoryDTO result = categoryService.updateCategory(id, dto);

        assertEquals("Updated Electronics", existing.getName());
        assertEquals("Updated description", existing.getDescription());
        assertEquals(expected, result);
        verify(categoryRepository).save(existing);
    }

    @Test
    void updateCategoryWithSameNameDoesNotCheckAvailability() {
        UUID id = UUID.randomUUID();

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Electronics");
        dto.setDescription("Updated description");

        CategoryEntity existing = new CategoryEntity();
        existing.setName("Electronics");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);
        when(categoryMapper.toDTO(existing)).thenReturn(dto);

        categoryService.updateCategory(id, dto);

        verify(categoryRepository, never()).existsByName(anyString());
    }

    @Test
    void updateCategoryWithExistingNameThrowsException() {
        UUID id = UUID.randomUUID();

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Books");

        CategoryEntity existing = new CategoryEntity();
        existing.setName("Electronics");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("Books")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.updateCategory(id, dto)
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategoryThrowsExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> categoryService.updateCategory(id, new CategoryDTO())
        );
    }

    @Test
    void deleteCategorySuccessfully() {
        UUID id = UUID.randomUUID();
        CategoryEntity category = new CategoryEntity();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByIdAndProductsIsNotEmpty(id)).thenReturn(false);

        categoryService.deleteCategory(id);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategoryWithProductsThrowsException() {
        UUID id = UUID.randomUUID();
        CategoryEntity category = new CategoryEntity();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByIdAndProductsIsNotEmpty(id)).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.deleteCategory(id)
        );

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategoryThrowsExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> categoryService.deleteCategory(id)
        );

        verify(categoryRepository, never()).delete(any());
    }
}
