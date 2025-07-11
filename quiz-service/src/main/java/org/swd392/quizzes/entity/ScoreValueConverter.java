package org.swd392.quizzes.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ScoreValueConverter implements AttributeConverter<QuizOptions.ScoreValue, Integer> {
    @Override
    public Integer convertToDatabaseColumn(QuizOptions.ScoreValue attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public QuizOptions.ScoreValue convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        return QuizOptions.ScoreValue.fromValue(dbData);
    }
}