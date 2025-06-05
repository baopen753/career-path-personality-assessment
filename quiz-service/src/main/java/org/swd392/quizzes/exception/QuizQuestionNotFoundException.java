package org.swd392.quizzes.exception;

public class QuizQuestionNotFoundException extends RuntimeException {
    public QuizQuestionNotFoundException(String message) {
        super(message);
    }

    public QuizQuestionNotFoundException(Long id) {
        super("Quiz question not found with id: " + id);
    }
}
