package org.baopen753.quizzes.enums;

public enum ScoreValue {
    DISAGREE(-1),
    NEUTRAL(0),
    AGREE(1);

    private final int value;

    ScoreValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
