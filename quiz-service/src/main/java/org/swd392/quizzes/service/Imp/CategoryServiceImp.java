package org.swd392.quizzes.service.Imp;

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
import org.swd392.quizzes.service.CategoryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.debug("Fetching all categories");

        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        log.debug("Fetching category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        return convertToDTO(category);
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        log.info("Updating category with ID: {}", id);

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        validateCategoryDTO(categoryDTO);

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

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}