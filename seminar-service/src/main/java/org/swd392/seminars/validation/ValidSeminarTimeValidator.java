package org.swd392.seminars.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.swd392.seminars.payload.request.SeminarRequest;

import java.time.LocalDateTime;

public class ValidSeminarTimeValidator implements ConstraintValidator<ValidSeminarTime, SeminarRequest> {

    @Override
    public void initialize(ValidSeminarTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(SeminarRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getStartingTime() == null || request.getEndingTime() == null) {
            return true; // Let @NotNull handle null validation
        }
        
        LocalDateTime startingTime = request.getStartingTime();
        LocalDateTime endingTime = request.getEndingTime();
        
        return !startingTime.isAfter(endingTime);
    }
} 