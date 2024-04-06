package app.linguistai.bmvp.utils;

import app.linguistai.bmvp.model.gamification.quest.types.QuestCompletionCriteria;
import app.linguistai.bmvp.model.gamification.quest.types.UseWordCriteria;
import app.linguistai.bmvp.model.gamification.quest.types.SendMessageCriteria;
import app.linguistai.bmvp.model.gamification.quest.types.CreateWordListCriteria;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * @author ChatGPT, will be refactored when merging to dev
 */
@Converter(autoApply = true)
public class QuestCompletionCriteriaConverter implements AttributeConverter<QuestCompletionCriteria, String> {

    @Override
    public String convertToDatabaseColumn(QuestCompletionCriteria attribute) {
        if (attribute == null) {
            return null;
        }

        if (attribute instanceof UseWordCriteria) {
            UseWordCriteria criteria = (UseWordCriteria) attribute;
            return "UseWord:" + criteria.getWord() + "," + criteria.getTimes();
        }
        else if (attribute instanceof SendMessageCriteria) {
            SendMessageCriteria criteria = (SendMessageCriteria) attribute;
            return "SendMessage:" + criteria.getTimes();
        }
        else if (attribute instanceof CreateWordListCriteria) {
            CreateWordListCriteria criteria = (CreateWordListCriteria) attribute;
            return "CreateWordList:" + criteria.getTimes();
        }

        return attribute.toString(); // Default serialization
    }

    @Override
    public QuestCompletionCriteria convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        String[] parts = dbData.split(":");
        if (parts.length < 2) {
            return null; // or throw an IllegalArgumentException
        }

        String type = parts[0];
        String[] details = parts[1].split(",");

        switch (type) {
            case "UseWord":
                if (details.length < 2) {
                    return null; // or handle error
                }
                return new UseWordCriteria(details[0], Integer.parseInt(details[1]));
            case "SendMessage":
                return new SendMessageCriteria(Integer.parseInt(details[0]));
            case "CreateWordList":
                return new CreateWordListCriteria(Integer.parseInt(details[0]));
            default:
                return null; // or handle unknown types
        }
    }
}
