package org.baopen753.quizzes.enums;

public enum MBTIDimension {
    EXTROVERSION_INTROVERSION("E-I", "Extroversion vs Introversion"),
    SENSING_INTUITION("S-N", "Sensing vs Intuition"),
    THINKING_FEELING("T-F", "Thinking vs Feeling"),
    JUDGING_PERCEIVING("J-P", "Judging vs Perceiving");

    private final String code;
    private final String description;

    MBTIDimension(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    
} 