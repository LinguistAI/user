package app.linguistai.bmvp.model.gamification.quest.types;

import lombok.Data;

@Data
public class UseWordCriteria extends QuestCompletionCriteria {
    private String word;
    private Integer times;
}
