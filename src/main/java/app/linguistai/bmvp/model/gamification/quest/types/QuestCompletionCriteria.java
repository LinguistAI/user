package app.linguistai.bmvp.model.gamification.quest.types;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import static app.linguistai.bmvp.consts.QuestConsts.TIMES_CLOSING_IDENTIFIER;
import static app.linguistai.bmvp.consts.QuestConsts.TIMES_IDENTIFIER;

@Data
@RequiredArgsConstructor
public class QuestCompletionCriteria {
    private final Integer times;

    public String toString() {
        return TIMES_IDENTIFIER + this.getTimes().toString() + TIMES_CLOSING_IDENTIFIER;
    }
}
