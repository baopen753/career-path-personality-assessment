package org.swd392.quizzes.exception;

public class InvalidQuizSubmissionException extends RuntimeException {
    public InvalidQuizSubmissionException(String message) {
        super(message);
    }
}
