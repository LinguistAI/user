package app.linguistai.bmvp.service.gamification.quest;

import app.linguistai.bmvp.configs.QuestConfiguration;
import app.linguistai.bmvp.repository.gamification.quest.IQuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class QuestLoader implements ApplicationRunner {
    private final IQuestRepository questRepository;

    private final QuestConfiguration questConfiguration;

    @Autowired
    public QuestLoader(IQuestRepository questRepository, QuestConfiguration questConfiguration) {
        this.questRepository = questRepository;
        this.questConfiguration = questConfiguration;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            questRepository.saveAll(questConfiguration.getQuests());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
