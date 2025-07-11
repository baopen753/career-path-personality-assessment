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
     * Get category by name
     */
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryByName(String name) {
        log.debug("Fetching category with name: {}", name);

        Optional<Category> categoryOpt = categoryRepository.findByName(name);
        return categoryOpt.map(this::convertToDTO);
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
     * Get categories with quiz count
     */
    @Transactional(readOnly = true)
    public List<CategoryWithQuizCountDTO> getCategoriesWithQuizCount() {
        log.debug("Fetching categories with quiz count");

        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> {
                    List<Quiz> quizzes = quizRepository.findByCategoryId(category.getId());

                    CategoryWithQuizCountDTO dto = new CategoryWithQuizCountDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    dto.setDescription(category.getDescription());
                    dto.setQuizCount(quizzes.size());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get category statistics for admin dashboard
     */
    @Transactional(readOnly = true)
    public CategoryStatisticsDTO getCategoryStatistics() {
        log.debug("Generating category statistics");

        List<Category> allCategories = categoryRepository.findAll();
        CategoryStatisticsDTO stats = new CategoryStatisticsDTO();

        stats.setTotalCategories(allCategories.size());

        // Calculate categories with and without quizzes
        int categoriesWithQuizzes = 0;
        int totalQuizzes = 0;

        for (Category category : allCategories) {
            List<Quiz> quizzes = quizRepository.findByCategoryId(category.getId());
            if (!quizzes.isEmpty()) {
                categoriesWithQuizzes++;
                totalQuizzes += quizzes.size();
            }
        }

        stats.setCategoriesWithQuizzes(categoriesWithQuizzes);
        stats.setCategoriesWithoutQuizzes(allCategories.size() - categoriesWithQuizzes);
        stats.setTotalQuizzes(totalQuizzes);
        stats.setAverageQuizzesPerCategory(allCategories.isEmpty() ? 0.0 : (double) totalQuizzes / allCategories.size());

        return stats;
    }

    /**
     * Search categories by name (case-insensitive partial match)
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> searchCategoriesByName(String searchTerm) {
        log.debug("Searching categories with term: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCategories();
        }

        // Since we don't have a custom search method in repository, we'll filter in memory
        // In a real application, you might want to add a custom repository method
        List<Category> allCategories = categoryRepository.findAll();

        return allCategories.stream()
                .filter(category -> category.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        (category.getDescription() != null &&
                                category.getDescription().toLowerCase().contains(searchTerm.toLowerCase())))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

    // Inner classes for response DTOs
    public static class CategoryWithQuizCountDTO {
        private Long id;
        private String name;
        private String description;
        private Integer quizCount;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getQuizCount() { return quizCount; }
        public void setQuizCount(Integer quizCount) { this.quizCount = quizCount; }
    }

    public static class CategoryStatisticsDTO {
        private Integer totalCategories;
        private Integer categoriesWithQuizzes;
        private Integer categoriesWithoutQuizzes;
        private Integer totalQuizzes;
        private Double averageQuizzesPerCategory;

        // Getters and setters
        public Integer getTotalCategories() { return totalCategories; }
        public void setTotalCategories(Integer totalCategories) { this.totalCategories = totalCategories; }

        public Integer getCategoriesWithQuizzes() { return categoriesWithQuizzes; }
        public void setCategoriesWithQuizzes(Integer categoriesWithQuizzes) { this.categoriesWithQuizzes = categoriesWithQuizzes; }

        public Integer getCategoriesWithoutQuizzes() { return categoriesWithoutQuizzes; }
        public void setCategoriesWithoutQuizzes(Integer categoriesWithoutQuizzes) { this.categoriesWithoutQuizzes = categoriesWithoutQuizzes; }

        public Integer getTotalQuizzes() { return totalQuizzes; }
        public void setTotalQuizzes(Integer totalQuizzes) { this.totalQuizzes = totalQuizzes; }

        public Double getAverageQuizzesPerCategory() { return averageQuizzesPerCategory; }
        public void setAverageQuizzesPerCategory(Double averageQuizzesPerCategory) { this.averageQuizzesPerCategory = averageQuizzesPerCategory; }
    }
}