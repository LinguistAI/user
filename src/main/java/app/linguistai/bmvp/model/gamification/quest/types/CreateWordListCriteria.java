package app.linguistai.bmvp.model.gamification.quest.types;

import lombok.Data;

@Data
public class CreateWordListCriteria extends QuestCompletionCriteria {
    private Integer times;
}
