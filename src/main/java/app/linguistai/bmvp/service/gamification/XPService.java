package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.configs.XPConfiguration;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.UserXPNotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.XPAction;
import app.linguistai.bmvp.model.gamification.UserXP;
import app.linguistai.bmvp.model.gamification.UserXPWithUser;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.gamification.IUserXPRepository;
import app.linguistai.bmvp.response.gamification.RUserXP;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.TreeMap;

@RequiredArgsConstructor
@Service
public class XPService implements IXPService {

    private final XPConfiguration xp;

    private final IAccountRepository accountRepository;

    private final IUserXPRepository xpRepository;

    @Override
    public RUserXP createUserXP(String email) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

            UserXP toSave = new UserXP();
            toSave.setUser(user);
            toSave.setExperience(0L);

            // Create and save new UserXP
            UserXP saved = xpRepository.save(toSave);

            return RUserXP.builder()
                .username(user.getUsername())
                .experience(saved.getExperience())
                .level(1L)
                .build();
        }
        catch (Exception e1) {
            System.out.println("ERROR: Could not create user xp.");
            throw e1;
        }
    }

    @Override
    public RUserXP increaseUserXP(String email, XPAction action) throws Exception {
        try {
            UserXPWithUser info = this.getUserOwnedXP(email);
            UserXP userXP = info.userXP();
            User user = info.user();

            userXP.setExperience(userXP.getExperience() + xp.getXP(action.key()));

            UserXP updated = xpRepository.save(userXP);

            return RUserXP.builder()
                .username(user.getUsername())
                .experience(updated.getExperience())
                .level(this.determineProceduralLevel(updated.getExperience()))
                .build();
        }
        catch (UserXPNotFoundException e1) {
            return this.createUserXP(email);
        }
        catch (Exception e2) {
            System.out.println("ERROR: Could not increase user xp.");
            throw e2;
        }
    }

    @Override
    public RUserXP getUserXP(String email) throws Exception {
        try {
            UserXPWithUser info = this.getUserOwnedXP(email);
            UserXP userXP = info.userXP();
            User user = info.user();

            return RUserXP.builder()
                .username(user.getUsername())
                .experience(userXP.getExperience())
                .level(this.determineProceduralLevel(userXP.getExperience()))
                .build();
        }
        catch (UserXPNotFoundException e1) {
            return this.createUserXP(email);
        }
        catch (Exception e2) {
            System.out.println("ERROR: Could not increase user xp.");
            throw e2;
        }
    }

    @Transactional
    protected UserXPWithUser getUserOwnedXP(String email) throws Exception {
        // Check if user exists
        User user = accountRepository.findUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("User does not exist for given email: [" + email + "]."));

        // Check if UserXP exists
        UserXP userXP = xpRepository.findById(user.getId())
            .orElseThrow(() -> new UserXPNotFoundException("UserXP does not exist for given userId: [" + user.getId() + "]."));

        // Check if user is owner of the UserXP
        if (userXP.getUser().getId() != user.getId()) {
            throw new Exception("User not authorized to modify list.");
        }

        return new UserXPWithUser(user, userXP);
    }

    @Deprecated
    private Long determineHardcodedLevel(Long points) throws Exception {
        TreeMap<Long, String> sortedLevels = new TreeMap<>();
        xp.getLevels().forEach((key, value) -> sortedLevels.put(value.longValue(), key));

        // Find the highest level that the given XP points meets or exceeds
        Map.Entry<Long, String> entry = sortedLevels.floorEntry(points);
        if (entry != null) {
            // Extract the level number from the key
            String levelKey = entry.getValue();
            try {
                return Long.parseLong(levelKey.replaceAll("\\D+", ""));
            }
            catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid level key format: " + levelKey);
            }
        }

        return 1L;
    }

    /**
     * Calculates the current level of the user.
     * Each level requires more xp to progress to based on the following equation:
     * xp required to progress to next level = baseLevel * (levelCoefficient)^(currentLevel - 1)
     * @param points current xp of user
     * @return current level of user
     */
    private Long determineProceduralLevel(Long points) {
        Long baseLevel = xp.getBaseLevel(); // level required to progress from level 1 to level 2
        Long levelCoefficient = xp.getLevelCoefficient(); // the coefficient for increasing required xp after each level
        Long level = 1L; // current level
        Double xpThreshold = Double.valueOf(baseLevel); // converted to double for multiplication

        while (points >= xpThreshold) {
            level++;
            xpThreshold *= levelCoefficient;
        }

        return level;
    }

}

