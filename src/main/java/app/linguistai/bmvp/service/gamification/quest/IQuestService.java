package app.linguistai.bmvp.service.gamification.quest;

import app.linguistai.bmvp.model.gamification.quest.types.QuestCompletionCriteria;
import app.linguistai.bmvp.request.gamification.QQuestSendMessage;
import app.linguistai.bmvp.request.gamification.QQuestTypeAction;

public interface IQuestService {
    void processSendMessage(String email, QQuestSendMessage message) throws Exception;
    void processQuestTypeAction(String email, QuestCompletionCriteria type, QQuestTypeAction action) throws Exception;
    Boolean checkUserHasQuestType(String email, QuestCompletionCriteria type) throws Exception;
    void assignQuests(String email) throws Exception;
}
