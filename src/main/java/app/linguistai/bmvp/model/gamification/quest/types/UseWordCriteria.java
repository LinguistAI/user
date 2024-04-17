package app.linguistai.bmvp.model.gamification.quest.types;

import lombok.Getter;
import lombok.Setter;

import static app.linguistai.bmvp.consts.QuestConsts.WORD_CLOSING_IDENTIFIER;
import static app.linguistai.bmvp.consts.QuestConsts.WORD_IDENTIFIER;

@Getter
@Setter
public class UseWordCriteria extends QuestCompletionCriteria {
    private final String word;

    public UseWordCriteria(String word, Integer times) {
        super(times);
        this.word = word;
    }

    @Override
    public String toString() {
        return super.toString() + WORD_IDENTIFIER + this.getWord() + WORD_CLOSING_IDENTIFIER;
    }
}
