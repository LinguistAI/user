package app.linguistai.bmvp.utils;

import app.linguistai.bmvp.model.gamification.quest.types.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class QuestProgressConverter implements AttributeConverter<QuestProgress, String> {

    @Override
    public String convertToDatabaseColumn(QuestProgress attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.toString();
    }

    @Override
    public QuestProgress convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        int times = Integer.parseInt(dbData);

        return new QuestProgress(times);
    }
}
