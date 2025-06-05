package org.swd392.quizzes.exception;

/**
 * Exception thrown when a requested personality standard is not found.
 * This can occur if the standard does not exist in the database or if the provided ID or code is invalid.
 */

public class PersonalityStandardNotFoundException extends RuntimeException {

    public PersonalityStandardNotFoundException(Long id) {
        super("Personality standard not found with id: " + id);
    }

    // Remove this constructor to avoid conflict:
    // public PersonalityStandardNotFoundException(String personalityCode)

    public static PersonalityStandardNotFoundException forCode(String personalityCode) {
        return new PersonalityStandardNotFoundException("Personality standard not found with code: " + personalityCode);
    }

    public PersonalityStandardNotFoundException(String message) {
        super(message);
    }
}

