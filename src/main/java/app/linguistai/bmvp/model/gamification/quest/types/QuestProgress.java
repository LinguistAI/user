package app.linguistai.bmvp.model.gamification.quest.types;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class QuestProgress {
    private final Integer times;

    public String toString() {
        return this.getTimes().toString();
    }
}
