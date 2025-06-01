package org.swd392.quizzes.exception;

public class QuizNotFoundException extends RuntimeException {
    public QuizNotFoundException(String message) {
        super(message);
    }

    public QuizNotFoundException(Long id) {
        super("Quiz not found with id: " + id);
    }
}