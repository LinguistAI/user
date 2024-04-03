package app.linguistai.bmvp.service.gamification.quest;

import app.linguistai.bmvp.request.gamification.QQuestSendMessage;

public interface IQuestService {
    void processSendMessage(String email, QQuestSendMessage message);
}
