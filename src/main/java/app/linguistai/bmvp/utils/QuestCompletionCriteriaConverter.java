package app.linguistai.bmvp.utils;

import app.linguistai.bmvp.model.gamification.quest.types.QuestCompletionCriteria;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class QuestCompletionCriteriaConverter implements AttributeConverter<QuestCompletionCriteria, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(QuestCompletionCriteria attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert quest completion criteria to JSON string", e);
        }
    }

    @Override
    public QuestCompletionCriteria convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, QuestCompletionCriteria.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert JSON string to quest completion criteria", e);
        }
    }
}
