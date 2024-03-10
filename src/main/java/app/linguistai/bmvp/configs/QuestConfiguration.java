package app.linguistai.bmvp.configs;

import app.linguistai.bmvp.model.gamification.quest.Quest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "quests")
public class QuestConfiguration {
    private List<Quest> quests = new ArrayList<>();
}

