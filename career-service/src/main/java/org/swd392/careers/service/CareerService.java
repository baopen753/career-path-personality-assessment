package org.swd392.careers.service;

import org.swd392.careers.entity.Career;
import org.swd392.careers.repository.CareerRepository;
import org.swd392.careers.dto.CareerDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CareerService {
    @Autowired
    private CareerRepository careerRepository;

    public Career createCareer(CareerDTO careerDTO) {
        Career career = new Career();
        BeanUtils.copyProperties(careerDTO, career);
        return careerRepository.save(career);
    }

    public List<Career> getAllCareers() {
        return careerRepository.findAll();
    }

    public Optional<Career> getCareerById(String id) {
        return careerRepository.findById(id);
    }

    public Career updateCareer(String id, CareerDTO careerDTO) {
        Optional<Career> existingCareer = careerRepository.findById(id);
        if (existingCareer.isPresent()) {
            Career career = existingCareer.get();
            BeanUtils.copyProperties(careerDTO, career);
            return careerRepository.save(career);
        }
        return null;
    }

    public void deleteCareer(String id) {
        careerRepository.deleteById(id);
    }
}