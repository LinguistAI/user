package app.linguistai.bmvp.repository.gamification.quest;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.quest.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IQuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findAllByUserId(UUID userId);
    void deleteAllByUser(User user);
}
