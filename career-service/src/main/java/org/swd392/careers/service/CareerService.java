package org.swd392.careers.service;

import org.swd392.careers.entity.Career;
import org.swd392.careers.repository.CareerRepository;
import org.swd392.careers.dto.CareerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CareerService {
    @Autowired
    private CareerRepository careerRepository;

    public Career createCareer(CareerDTO dto) {
        Career career = new Career();
        career.setName(dto.getName());
        career.setDescription(dto.getDescription());
        return careerRepository.save(career);
    }

    public List<Career> getAllCareers() {
        return careerRepository.findAll();
    }

    public Optional<Career> getCareerById(String id) {
        return careerRepository.findById(id);
    }

    public Career updateCareer(String id, CareerDTO dto) {
        return careerRepository.findById(id)
                .map(career -> {
                    career.setName(dto.getName());
                    career.setDescription(dto.getDescription());
                    return careerRepository.save(career);
                })
                .orElse(null);
    }

    public void deleteCareer(String id) {
        careerRepository.deleteById(id);
    }

    // New methods to support quiz-service integration
    public List<Career> getCareersByPersonality(String personalityType) {
        // This could be enhanced with actual personality matching logic
        return careerRepository.findByNameContainingIgnoreCase(personalityType);
    }

    public List<Career> searchCareersByPersonalityTypes(List<String> personalityTypes) {
        // You could implement more sophisticated matching logic here
        return careerRepository.findByNameInIgnoreCase(personalityTypes);
    }

    public List<Career> searchCareersByNames(List<String> careerNames) {
        return careerRepository.findByNameInIgnoreCase(careerNames);
    }
}