package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.XPAction;
import app.linguistai.bmvp.response.gamification.RUserXP;

public interface IXPService {
    RUserXP createUserXP(String email) throws Exception;
    void createUserXPForRegister(User user) throws Exception;
    RUserXP increaseUserXP(String email, XPAction action) throws Exception;
    void awardQuestReward(String email, Long reward) throws Exception;
    // Get user xp
    RUserXP getUserXP(String email) throws Exception;
    // (?) Decrease user xp
}
