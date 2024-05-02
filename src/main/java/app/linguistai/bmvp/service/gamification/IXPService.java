package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.enums.XPAction;
import app.linguistai.bmvp.response.gamification.RUserXP;

import java.util.UUID;

public interface IXPService {
    RUserXP createUserXP(String email) throws Exception;
    void createUserXPForRegister(User user) throws Exception;
    RUserXP increaseUserXP(String email, XPAction action) throws Exception;
    void awardQuestReward(String email, Long reward) throws Exception;
    // Get user xp
    RUserXP getUserXPByEmail(String email) throws Exception;
    RUserXP getUserXPById(UUID userId) throws Exception;
    Long getUserGlobalRank(User user) throws Exception;
    // (?) Decrease user xp
}
