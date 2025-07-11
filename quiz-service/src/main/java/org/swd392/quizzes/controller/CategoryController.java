package org.swd392.quizzes.controller;

import org.swd392.quizzes.dto.CategoryDTO;
import org.swd392.quizzes.service.CategoryService;
import org.swd392.quizzes.service.CategoryService.CategoryStatisticsDTO;
import org.swd392.quizzes.service.CategoryService.CategoryWithQuizCountDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
     //Get all categories
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.info("Fetching all categories");

        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

//Get category by ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        log.info("Fetching category with ID: {}", id);

        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

//Get category by name
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String name) {
        log.info("Fetching category with name: {}", name);

        Optional<CategoryDTO> category = categoryService.getCategoryByName(name);

        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//Create a new category (Admin only)
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        log.info("Creating new category: {}", categoryDTO.getName());

        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

 //Update an existing category (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        log.info("Updating category with ID: {}", id);

        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

//Delete a category (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("Deleting category with ID: {}", id);

        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

//Check if category exists
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> categoryExists(@PathVariable Long id) {
        log.info("Checking existence of category with ID: {}", id);

        boolean exists = categoryService.categoryExists(id);
        return ResponseEntity.ok(exists);
    }

//Check if category name is available
    @GetMapping("/name/{name}/available")
    public ResponseEntity<Boolean> isCategoryNameAvailable(@PathVariable String name) {
        log.info("Checking availability of category name: {}", name);

        boolean available = categoryService.isCategoryNameAvailable(name);
        return ResponseEntity.ok(available);
    }

//Check if category name is available for update
    @GetMapping("/name/{name}/available-for-update/{excludeId}")
    public ResponseEntity<Boolean> isCategoryNameAvailableForUpdate(
            @PathVariable String name,
            @PathVariable Long excludeId) {
        log.info("Checking availability of category name: {} for update (excluding ID: {})", name, excludeId);

        boolean available = categoryService.isCategoryNameAvailableForUpdate(name, excludeId);
        return ResponseEntity.ok(available);
    }

 //Get categories with quiz count
    @GetMapping("/with-quiz-count")
    public ResponseEntity<List<CategoryWithQuizCountDTO>> getCategoriesWithQuizCount() {
        log.info("Fetching categories with quiz count");

        List<CategoryWithQuizCountDTO> categories = categoryService.getCategoriesWithQuizCount();
        return ResponseEntity.ok(categories);
    }

//Get category statistics (Admin dashboard)
    @GetMapping("/statistics")
    public ResponseEntity<CategoryStatisticsDTO> getCategoryStatistics() {
        log.info("Fetching category statistics");

        CategoryStatisticsDTO statistics = categoryService.getCategoryStatistics();
        return ResponseEntity.ok(statistics);
    }

//Search categories by name
    @GetMapping("/search")
    public ResponseEntity<List<CategoryDTO>> searchCategories(@RequestParam String searchTerm) {
        log.info("Searching categories with term: {}", searchTerm);

        List<CategoryDTO> categories = categoryService.searchCategoriesByName(searchTerm);
        return ResponseEntity.ok(categories);
    }

//Get categories for dropdown/select (simplified response)
    @GetMapping("/dropdown")
    public ResponseEntity<List<CategoryDropdownDTO>> getCategoriesForDropdown() {
        log.info("Fetching categories for dropdown");

        List<CategoryDTO> categories = categoryService.getAllCategories();
        List<CategoryDropdownDTO> dropdownCategories = categories.stream()
                .map(category -> new CategoryDropdownDTO(category.getId(), category.getName()))
                .toList();

        return ResponseEntity.ok(dropdownCategories);
    }

//Bulk delete categories (Admin only)
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDeleteCategories(@RequestBody List<Long> categoryIds) {
        log.info("Bulk deleting categories: {}", categoryIds);

        for (Long id : categoryIds) {
            try {
                categoryService.deleteCategory(id);
            } catch (Exception e) {
                log.warn("Failed to delete category with ID: {}", id, e);
                // Continue with other deletions
            }
        }

        return ResponseEntity.noContent().build();
    }

//Exception handler for this controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error in CategoryController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the request: " + e.getMessage());
    }

//Inner class for dropdown response
    public static class CategoryDropdownDTO {
        private Long id;
        private String name;

        public CategoryDropdownDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}