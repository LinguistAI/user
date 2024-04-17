package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.configs.XPConfiguration;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.exception.UserXPNotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.XPAction;
import app.linguistai.bmvp.model.gamification.UserXP;
import app.linguistai.bmvp.model.gamification.UserXPWithLevel;
import app.linguistai.bmvp.model.gamification.UserXPWithUser;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.gamification.IUserXPRepository;
import app.linguistai.bmvp.response.gamification.RUserXP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class XPService implements IXPService {

    private final XPConfiguration xp;

    private final IAccountRepository accountRepository;

    private final IUserXPRepository xpRepository;

    @Override
    @Transactional
    public RUserXP createUserXP(String email) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User does not exist"));

            UserXP toSave = new UserXP();
            toSave.setUser(user);
            toSave.setExperience(0L);

            // Create and save new UserXP
            UserXP saved = xpRepository.save(toSave);
            log.info("User XP created for email {}", email);

            UserXPWithLevel levelInfo = this.determineProceduralLevel(saved.getExperience());
            Long userLevel = levelInfo.level();
            Long xpToNextLevel = levelInfo.totalExperienceToNextLevel();

            return RUserXP.builder()
                .username(user.getUsername())
                .currentExperience(saved.getExperience())
                .totalExperienceToNextLevel(xpToNextLevel)
                .level(userLevel)
                .build();
        }
        catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        }
        catch (Exception e1) {
            log.error("Create user XP failed for email {}", email, e1);
            throw new SomethingWentWrongException();
        }
    }

    @Override
    public void createUserXPForRegister(User user) throws Exception {
        try {
            UserXP toSave = new UserXP();
            toSave.setUser(user);
            toSave.setExperience(0L);

            // Create and save new UserXP
            xpRepository.save(toSave);
            log.info("User XP created for email {}", user.getEmail());
        }
        catch (Exception e1) {
            log.error("Create user XP for register failed for user with email {}", user.getEmail(), e1);
            throw new SomethingWentWrongException();
        }
    }

    @Override
    @Transactional
    public RUserXP increaseUserXP(String email, XPAction action) throws Exception {
        try {
            UserXPWithUser info = this.getUserOwnedXP(email);
            UserXP userXP = info.userXP();
            User user = info.user();

            userXP.setExperience(userXP.getExperience() + xp.getXP(action.key()));

            UserXP updated = xpRepository.save(userXP);

            UserXPWithLevel levelInfo = this.determineProceduralLevel(updated.getExperience());
            Long userLevel = levelInfo.level();
            Long xpToNextLevel = levelInfo.totalExperienceToNextLevel();

            return RUserXP.builder()
                .username(user.getUsername())
                .currentExperience(updated.getExperience())
                .totalExperienceToNextLevel(xpToNextLevel)
                .level(userLevel)
                .build();
        }
        catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        }
        catch (UserXPNotFoundException e1) {
            return this.createUserXP(email);
        }
        catch (Exception e2) {
            log.error("Could not increase user XP", e2);
            throw new SomethingWentWrongException();
        }
    }

    @Override
    @Transactional
    public RUserXP getUserXP(String email) throws Exception {
        try {
            UserXPWithUser info = this.getUserOwnedXP(email);
            UserXP userXP = info.userXP();
            User user = info.user();

            UserXPWithLevel levelInfo = this.determineProceduralLevel(userXP.getExperience());
            Long userLevel = levelInfo.level();
            Long xpToNextLevel = levelInfo.totalExperienceToNextLevel();

            return RUserXP.builder()
                .username(user.getUsername())
                .currentExperience(userXP.getExperience())
                .totalExperienceToNextLevel(xpToNextLevel)
                .level(userLevel)
                .build();
        }
        catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        }
        catch (UserXPNotFoundException e1) {
            return this.createUserXP(email);
        }
        catch (Exception e2) {
            log.error("Could not get user XP", e2);
            throw new SomethingWentWrongException();
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
    private UserXPWithLevel determineProceduralLevel(Long points) {
        Long baseLevel = xp.getBaseLevel(); // level required to progress from level 1 to level 2
        Long levelCoefficient = xp.getLevelCoefficient(); // the coefficient for increasing required xp after each level
        Long level = 1L; // current level
        Double xpThreshold = Double.valueOf(baseLevel); // converted to double for multiplication

        while (points >= xpThreshold) {
            level++;
            xpThreshold *= levelCoefficient;
        }

        return new UserXPWithLevel(level, points, xpThreshold.longValue());
    }

}

