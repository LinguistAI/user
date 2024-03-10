package app.linguistai.bmvp.model.gamification.quest.types;

import lombok.Data;

@Data
public class SendMessageCriteria extends QuestCompletionCriteria {
    private Integer times;
}
