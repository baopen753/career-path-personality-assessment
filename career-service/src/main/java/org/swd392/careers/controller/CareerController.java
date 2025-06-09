package org.swd392.careers.controller;

import org.swd392.careers.dto.CareerDTO;
import org.swd392.careers.entity.Career;
import org.swd392.careers.service.CareerService;
import org.swd392.careers.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/careers")
@CrossOrigin
public class CareerController {
    @Autowired
    private CareerService careerService;

    @PostMapping
    public ResponseEntity<ApiResponse<Career>> createCareer(@RequestBody CareerDTO dto) {
        Career career = careerService.createCareer(dto);
        return ResponseEntity.ok(ApiResponse.<Career>builder()
                .message("Career created successfully")
                .result(career)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Career>>> getAllCareers() {
        List<Career> careers = careerService.getAllCareers();
        return ResponseEntity.ok(ApiResponse.<List<Career>>builder()
                .message("Careers retrieved successfully")
                .result(careers)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Career>> getCareerById(@PathVariable String id) {
        return careerService.getCareerById(id)
                .map(career -> ResponseEntity.ok(ApiResponse.<Career>builder()
                        .message("Career retrieved successfully")
                        .result(career)
                        .build()))
                .orElse(ResponseEntity.ok(ApiResponse.<Career>builder()
                        .code(1001)
                        .message("Career not found")
                        .build()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Career>> updateCareer(@PathVariable String id, @RequestBody CareerDTO dto) {
        Career career = careerService.updateCareer(id, dto);
        if (career == null) {
            return ResponseEntity.ok(ApiResponse.<Career>builder()
                    .code(1001)
                    .message("Career not found")
                    .build());
        }
        return ResponseEntity.ok(ApiResponse.<Career>builder()
                .message("Career updated successfully")
                .result(career)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCareer(@PathVariable String id) {
        careerService.deleteCareer(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Career deleted successfully")
                .build());
    }
}