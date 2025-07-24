package org.swd392.seminars.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidSeminarTimeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSeminarTime {
    String message() default "Ending time must be after starting time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 