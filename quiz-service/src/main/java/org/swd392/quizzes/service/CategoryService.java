package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.CategoryDTO;
import org.swd392.quizzes.entity.Category;
import org.swd392.quizzes.entity.Quiz;
import org.swd392.quizzes.exception.CategoryNotFoundException;
import org.swd392.quizzes.exception.InvalidQuizSubmissionException;
import org.swd392.quizzes.repository.CategoryRepository;
import org.swd392.quizzes.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;

    /**
     * Get all categories
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.debug("Fetching all categories");

        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        log.debug("Fetching category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        return convertToDTO(category);
    }

    /**
     * Create a new category
     */
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("Creating new category: {}", categoryDTO.getName());

        validateCategoryDTO(categoryDTO);

        // Check if category with same name already exists
        Optional<Category> existingCategory = categoryRepository.findByName(categoryDTO.getName());
        if (existingCategory.isPresent()) {
            throw new InvalidQuizSubmissionException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        Category category = convertToEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with ID: {} and name: {}",
                savedCategory.getId(), savedCategory.getName());

        return convertToDTO(savedCategory);
    }

    /**
     * Update an existing category
     */
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        log.info("Updating category with ID: {}", id);

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        validateCategoryDTO(categoryDTO);

        // Check if another category with the same name exists (excluding current one)
        Optional<Category> categoryWithSameName = categoryRepository.findByName(categoryDTO.getName());
        if (categoryWithSameName.isPresent() && !categoryWithSameName.get().getId().equals(id)) {
            throw new InvalidQuizSubmissionException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        // Update category fields
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        Category savedCategory = categoryRepository.save(existingCategory);

        log.info("Category updated successfully with ID: {} and name: {}",
                savedCategory.getId(), savedCategory.getName());

        return convertToDTO(savedCategory);
    }

    /**
     * Delete a category
     */
    public void deleteCategory(Long id) {
        log.info("Attempting to delete category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        // Check if category is being used by any quizzes
        List<Quiz> quizzesUsingCategory = quizRepository.findByCategoryId(id);
        if (!quizzesUsingCategory.isEmpty()) {
            log.warn("Cannot delete category ID: {} - it's being used by {} quiz(es)",
                    id, quizzesUsingCategory.size());
            throw new InvalidQuizSubmissionException(
                    "Cannot delete category '" + category.getName() +
                            "' because it's being used by " + quizzesUsingCategory.size() + " quiz(es). " +
                            "Please reassign or delete those quizzes first.");
        }

        try {
            categoryRepository.delete(category);
            log.info("Category deleted successfully with ID: {} and name: {}",
                    id, category.getName());
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to delete category due to data integrity violation", e);
            throw new InvalidQuizSubmissionException(
                    "Cannot delete category because it's referenced by other data. " +
                            "Please ensure all related data is removed first.");
        }
    }

    /**
     * Check if category exists
     */
    @Transactional(readOnly = true)
    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }

    /**
     * Check if category name is available
     */
    @Transactional(readOnly = true)
    public boolean isCategoryNameAvailable(String name) {
        return categoryRepository.findByName(name).isEmpty();
    }

    /**
     * Check if category name is available for update (excluding current category)
     */
    @Transactional(readOnly = true)
    public boolean isCategoryNameAvailableForUpdate(String name, Long excludeId) {
        Optional<Category> existingCategory = categoryRepository.findByName(name);
        return existingCategory.isEmpty() || existingCategory.get().getId().equals(excludeId);
    }

    /**
     * Validate category DTO
     */
    private void validateCategoryDTO(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            throw new InvalidQuizSubmissionException("Category data cannot be null");
        }

        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Category name cannot be empty");
        }

        if (categoryDTO.getName().length() > 100) {
            throw new InvalidQuizSubmissionException("Category name cannot exceed 100 characters");
        }

        if (categoryDTO.getDescription() != null && categoryDTO.getDescription().length() > 500) {
            throw new InvalidQuizSubmissionException("Category description cannot exceed 500 characters");
        }
    }

    /**
     * Convert Category entity to DTO
     */
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    /**
     * Convert CategoryDTO to entity
     */
    private Category convertToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName().trim());
        category.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        return category;
    }
}