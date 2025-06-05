package org.swd392.quizzes.controller;
import com.sba301.quiz_service.dto.PersonalityStandardDTO;
import com.sba301.quiz_service.service.PersonalityStandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/personality-standards")
@RequiredArgsConstructor
@Tag(name = "Personality Standard Management", description = "API for managing personality standards")
public class PersonalityStandardController {

    private final PersonalityStandardService personalityStandardService;

    @GetMapping
    @Operation(summary = "Get all personality standards", description = "Retrieve all personality standards")
    public ResponseEntity<List<PersonalityStandardDTO>> getAllPersonalityStandards() {
        List<PersonalityStandardDTO> standards = personalityStandardService.getAllPersonalityStandards();
        return ResponseEntity.ok(standards);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get personality standard by ID", description = "Retrieve a specific personality standard by its ID")
    public ResponseEntity<PersonalityStandardDTO> getPersonalityStandardById(
            @Parameter(description = "Personality Standard ID") @PathVariable Long id) {
        PersonalityStandardDTO standard = personalityStandardService.getPersonalityStandardById(id);
        return ResponseEntity.ok(standard);
    }

    @GetMapping("/standard/{standardType}")
    @Operation(summary = "Get personality standards by type", description = "Retrieve personality standards by standard type (DISC, MBTI)")
    public ResponseEntity<List<PersonalityStandardDTO>> getByStandard(
            @Parameter(description = "Standard Type") @PathVariable PersonalityStandard.StandardType standardType) {
        List<PersonalityStandardDTO> standards = personalityStandardService.getByStandard(standardType);
        return ResponseEntity.ok(standards);
    }

    @GetMapping("/code/{personalityCode}")
    @Operation(summary = "Get personality standard by code", description = "Retrieve a personality standard by its personality code")
    public ResponseEntity<PersonalityStandardDTO> getByPersonalityCode(
            @Parameter(description = "Personality Code") @PathVariable String personalityCode) {
        PersonalityStandardDTO standard = personalityStandardService.getByPersonalityCode(personalityCode);
        return ResponseEntity.ok(standard);
    }

    @PostMapping("/create")
    @Operation(summary = "Create new personality standard", description = "Create a new personality standard")
    public ResponseEntity<PersonalityStandardDTO> createPersonalityStandard(
            @Valid @RequestBody PersonalityStandardDTO personalityStandardDTO) {
        PersonalityStandardDTO createdStandard = personalityStandardService.createPersonalityStandard(personalityStandardDTO);
        return new ResponseEntity<>(createdStandard, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update personality standard", description = "Update an existing personality standard")
    public ResponseEntity<PersonalityStandardDTO> updatePersonalityStandard(
            @Parameter(description = "Personality Standard ID") @PathVariable Long id,
            @Valid @RequestBody PersonalityStandardDTO personalityStandardDTO) {
        PersonalityStandardDTO updatedStandard = personalityStandardService.updatePersonalityStandard(id, personalityStandardDTO);
        return ResponseEntity.ok(updatedStandard);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete personality standard", description = "Delete a personality standard by ID")
    public ResponseEntity<Void> deletePersonalityStandard(
            @Parameter(description = "Personality Standard ID") @PathVariable Long id) {
        personalityStandardService.deletePersonalityStandard(id);
        return ResponseEntity.noContent().build();
    }
}

