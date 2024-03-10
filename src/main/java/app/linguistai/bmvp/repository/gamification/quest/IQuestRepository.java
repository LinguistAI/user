package app.linguistai.bmvp.repository.gamification.quest;

import app.linguistai.bmvp.model.gamification.quest.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IQuestRepository extends JpaRepository<Quest, Long> {
}
