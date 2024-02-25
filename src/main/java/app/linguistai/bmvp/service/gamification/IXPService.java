package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.model.enums.XPAction;
import app.linguistai.bmvp.response.gamification.RUserXP;

public interface IXPService {
    RUserXP increaseUserXP(String email, XPAction action) throws Exception;
    // Get user xp
    RUserXP getUserXP(String email) throws Exception;
    // (?) Decrease user xp
}
