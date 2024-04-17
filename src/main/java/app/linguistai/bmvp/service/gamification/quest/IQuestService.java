package app.linguistai.bmvp.service.gamification.quest;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.quest.Quest;
import app.linguistai.bmvp.request.gamification.QQuestAction;
import app.linguistai.bmvp.request.gamification.QQuestSendMessage;

import java.util.List;

public interface IQuestService {
    void processSendMessage(String email, QQuestSendMessage message) throws Exception;
    void processQuest(User user, Quest quest, QQuestAction action) throws Exception;
    void assignQuests(String email) throws Exception;
    List<Quest> getUserQuests(String email) throws Exception;
}
