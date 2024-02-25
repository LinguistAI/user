package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.response.gamification.RUserXP;

public interface IXPService {
    RUserXP increaseUserXP(String email) throws Exception;
    // Get user xp
    // (?) Decrease user xp
}
