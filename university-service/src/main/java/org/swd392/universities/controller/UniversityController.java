package org.swd392.universities.controller;

import org.swd392.universities.dto.ApiResponse;
import org.swd392.universities.dto.UniversityDTO;
import org.swd392.universities.entity.University;
import org.swd392.universities.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin
public class UniversityController {
    @Autowired
    private UniversityService universityService;

    @PostMapping
    public ResponseEntity<ApiResponse<University>> createUniversity(@RequestBody UniversityDTO dto) {
        University university = universityService.createUniversity(dto);
        return ResponseEntity.ok(ApiResponse.<University>builder()
                .message("University created successfully")
                .result(university)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<University>>> getAllUniversities() {
        List<University> universities = universityService.getAllUniversities();
        return ResponseEntity.ok(ApiResponse.<List<University>>builder()
                .message("Universities retrieved successfully")
                .result(universities)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<University>> getUniversityById(@PathVariable String id) {
        return universityService.getUniversityById(id)
                .map(university -> ResponseEntity.ok(ApiResponse.<University>builder()
                        .message("University retrieved successfully")
                        .result(university)
                        .build()))
                .orElse(ResponseEntity.ok(ApiResponse.<University>builder()
                        .code(1001)
                        .message("University not found")
                        .build()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<University>> updateUniversity(@PathVariable String id, @RequestBody UniversityDTO dto) {
        University university = universityService.updateUniversity(id, dto);
        if (university == null) {
            return ResponseEntity.ok(ApiResponse.<University>builder()
                    .code(1001)
                    .message("University not found")
                    .build());
        }
        return ResponseEntity.ok(ApiResponse.<University>builder()
                .message("University updated successfully")
                .result(university)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUniversity(@PathVariable String id) {
        universityService.deleteUniversity(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("University deleted successfully")
                .build());
    }
}