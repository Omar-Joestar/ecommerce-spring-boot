package com.example.ecommerce.catalog.service;

import com.example.ecommerce.catalog.dto.CategoryDTO;
import com.example.ecommerce.catalog.entity.CategoryEntity;
import com.example.ecommerce.catalog.mapper.CategoryMapper;
import com.example.ecommerce.catalog.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.toDTOList(categoryRepository.findAll());
    }

    @Transactional
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        checkCategoryNameAvailability(categoryDTO.getName());
        CategoryEntity categoryEntity = categoryMapper.toEntity(categoryDTO);
        categoryEntity = categoryRepository.save(categoryEntity);
        return categoryMapper.toDTO(categoryEntity);
    }

    private void checkCategoryNameAvailability(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category name already exists: " + name);
        }
    }

    public CategoryDTO getCategoryById(UUID id) {
        CategoryEntity categoryEntity= categoryRepository.findById(id).orElseThrow(()-> new RuntimeException("No Category With This ID"));
        return categoryMapper.toDTO(categoryEntity);
    }
    @Transactional
    public CategoryDTO updateCategory(UUID id, CategoryDTO categoryDTO) {
        CategoryEntity existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (!existingCategory.getName().equalsIgnoreCase(categoryDTO.getName())) {
            checkCategoryNameAvailability(categoryDTO.getName());
        }

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        CategoryEntity updatedCategory = categoryRepository.save(existingCategory);

        return categoryMapper.toDTO(updatedCategory);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (categoryRepository.existsByIdAndProductsIsNotEmpty(id)) {
            throw new IllegalArgumentException(
                    "Cannot delete category because it has products assigned to it."
            );
        }

        categoryRepository.delete(category);
    }

}
