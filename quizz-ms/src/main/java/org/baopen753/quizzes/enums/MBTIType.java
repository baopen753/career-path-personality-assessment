package org.baopen753.quizzes.enums;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum MBTIType {
    ISTJ("The Inspector", "Quiet, serious, responsible, and conscientious"),
    ISFJ("The Protector", "Quiet, friendly, responsible, and conscientious"),
    INFJ("The Counselor", "Seek meaning and connection in ideas, relationships, and material possessions"),
    INTJ("The Mastermind", "Independent, original, analytical, and determined"),
    ISTP("The Craftsman", "Tolerant and flexible, quiet observers until a problem appears"),
    ISFP("The Composer", "Quiet, friendly, sensitive, and kind"),
    INFP("The Healer", "Idealistic, loyal to their values and to people who are important to them"),
    INTP("The Architect", "Seek to develop logical explanations for everything that interests them"),
    ESTP("The Dynamo", "Flexible and tolerant, they take a pragmatic approach focused on immediate results"),
    ESFP("The Performer", "Outgoing, friendly, and accepting"),
    ENFP("The Champion", "Enthusiastic, creative, and full of energy"),
    ENTP("The Visionary", "Quick, ingenious, stimulating, alert, and outspoken"),
    ESTJ("The Supervisor", "Practical, realistic, matter-of-fact, and decisive"),
    ESFJ("The Provider", "Warmhearted, conscientious, and cooperative"),
    ENFJ("The Teacher", "Warm, empathetic, responsive, and responsible"),
    ENTJ("The Commander", "Frank, decisive, assume leadership readily");

    private final String title;
    private final String description;

    MBTIType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Enumerated(EnumType.STRING)
    public String getCode() {
        return name();
    }
} 