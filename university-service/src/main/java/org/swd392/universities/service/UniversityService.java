package com.sba301.university_service.service;

import com.sba301.university_service.dto.UniversityDTO;
import com.sba301.university_service.entity.University;
import com.sba301.university_service.repository.UniversityRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UniversityService {
    @Autowired
    private UniversityRepository universityRepository;

    public University createUniversity(UniversityDTO universityDTO) {
        University university = new University();
        BeanUtils.copyProperties(universityDTO, university);
        return universityRepository.save(university);
    }

    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    public Optional<University> getUniversityById(String id) {
        return universityRepository.findById(id);
    }

    public University updateUniversity(String id, UniversityDTO universityDTO) {
        Optional<University> existingUniversity = universityRepository.findById(id);
        if (existingUniversity.isPresent()) {
            University university = existingUniversity.get();
            BeanUtils.copyProperties(universityDTO, university);
            return universityRepository.save(university);
        }
        return null;
    }

    public void deleteUniversity(String id) {
        universityRepository.deleteById(id);
    }
}