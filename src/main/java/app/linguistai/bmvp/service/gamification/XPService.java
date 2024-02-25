package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.configs.XPConfiguration;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.XPAction;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.response.gamification.RUserXP;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static app.linguistai.bmvp.model.enums.XPAction.LOGIN;

@RequiredArgsConstructor
@Service
public class XPService implements IXPService {

    private final XPConfiguration xp;

    private final IAccountRepository accountRepository;

    public void rewardLoginXp() {
        int loginXp = xp.getXP(LOGIN.key());
        // Reward XP logic here
    }

    @Override
    public RUserXP increaseUserXP(String email, XPAction action) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            return null;
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not increase user xp.");
            throw e1;
        }
    }

    @Override
    public RUserXP getUserXP(String email) throws Exception {
        return null;
    }
}

