package org.swd392.careers.service;

import org.swd392.careers.dto.CareerDTO;
import org.swd392.careers.entity.Career;

import java.util.List;
import java.util.Optional;

public interface CareerService {
    Career createCareer(CareerDTO dto);
    List<Career> getAllCareers();
    Optional<Career> getCareerById(String id);
    Career updateCareer(String id, CareerDTO dto);
    void deleteCareer(String id);
    List<Career> getCareersByPersonality(String personalityType);
    List<Career> searchCareersByPersonalityTypes(List<String> personalityTypes);
    List<Career> searchCareersByNames(List<String> careerNames);
}
