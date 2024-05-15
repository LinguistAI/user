package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.configs.XPConfiguration;
import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.consts.ServiceUris;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.exception.UserXPNotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.enums.XPAction;
import app.linguistai.bmvp.model.gamification.UserXP;
import app.linguistai.bmvp.model.gamification.UserXPWithLevel;
import app.linguistai.bmvp.model.gamification.UserXPWithUser;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.gamification.IUserXPRepository;
import app.linguistai.bmvp.response.gamification.RUserXP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class XPService implements IXPService {

    private final XPConfiguration xp;

    private final IAccountRepository accountRepository;

    private final IUserXPRepository xpRepository;

    private WebClient xpServiceWebClient;
    private final WebClient.Builder xpServiceWebClientBuilder;

    @Value("${aws.service.base.url}")
    private String AWS_SERVICE_BASE_URL;

    private WebClient getXpServiceWebClient() {
        if (xpServiceWebClient == null) {
            xpServiceWebClient = xpServiceWebClientBuilder.baseUrl(AWS_SERVICE_BASE_URL).build();
        }
        return xpServiceWebClient;
    }


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
            // Current XP info
            UserXPWithUser info = this.getUserOwnedXPByEmail(email);
            UserXP userXP = info.userXP();
            Long previousLevel = this.determineProceduralLevel(userXP.getExperience()).level();
            User user = info.user();

            // Calculate new XP and update
            Long newExperience = userXP.getExperience() + xp.getXP(action.key());
            userXP.setExperience(newExperience);
            UserXP updated = xpRepository.save(userXP);

            UserXPWithLevel newLevelInfo = this.determineProceduralLevel(updated.getExperience());
            Long userLevel = newLevelInfo.level();
            Long xpToNextLevel = newLevelInfo.totalExperienceToNextLevel();
            this.notifyUserLevelUp(previousLevel, user, userLevel);

            log.info("User XP increased from {} to {} for user {}", userXP.getExperience(), updated.getExperience(), user.getId());
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


    private void notifyUserLevelUp(Long previousLevel, User user, Long userLevel) {
        // Send level up event to AWS
        if (userLevel > previousLevel) {
            Map<String, Object> requestBody = new HashMap<>();

            // Select target users
            List<String> targetUsers = new ArrayList<String>();
            targetUsers.add(user.getId().toString());
            
            // Prepare data for notification
            Map<String, Object> data = new HashMap<>();
            data.put("type", "LevelUp");
            data.put("previousLevel", previousLevel);
            data.put("currentLevel", userLevel);
            
            // Prepare request body
            requestBody.put("userIds", targetUsers);
            requestBody.put("title", "Level Up!");
            requestBody.put("notificationMessage", "Congrats, you've leveled up to level " + userLevel + "!");
            requestBody.put("data", data);                

            this.getXpServiceWebClient().post()
                .uri(ServiceUris.AWS_SERVICE_SEND_NOTIFICATION)
                .header(Header.USER_EMAIL, user.getEmail())
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> {
                    if (response != null) {
                        log.info("Successfully sent LevelUp Notification to SNS - Response from AWS service: " + response);
                    }
                }, error -> {
                    if (error != null) {
                        log.error("Failed to send LevelUp Notification to SNS - Error from AWS service: " + error.getMessage());
                    }
                });
            log.info("User {} leveled up to new level {} from level {}", user.getId(), userLevel, previousLevel);
        }
    }

    @Override
    @Transactional
    public void awardQuestReward(String email, Long reward) throws Exception {
        try {
            UserXPWithUser info = this.getUserOwnedXPByEmail(email);
            UserXP userXP = info.userXP();

            userXP.setExperience(userXP.getExperience() + reward);

            xpRepository.save(userXP);
        }
        catch (NotFoundException e) {
            log.error("User is not found for email {}", email);
            throw e;
        }
        catch (Exception e2) {
            log.error("Could not increase user XP", e2);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public RUserXP getUserXP(UserXPWithUser info) throws Exception {
        try {
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
        catch (Exception e2) {
            log.error("Could not get user XP", e2);
            throw new SomethingWentWrongException();
        }
    }

    @Override
    @Transactional
    public RUserXP getUserXPByEmail(String email) throws Exception {
        try {
            UserXPWithUser info = this.getUserOwnedXPByEmail(email);
            return getUserXP(info);
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

    @Override
    @Transactional
    public RUserXP getUserXPById(UUID uuid) throws Exception {
        try {
            UserXPWithUser info = this.getUserOwnedXPById(uuid);

            return getUserXP(info);
        }
        catch (Exception e2) {
            log.error("Could not get user XP", e2);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    protected UserXPWithUser getUserOwnedXPByEmail(String email) throws Exception {
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

    @Transactional
    protected UserXPWithUser getUserOwnedXPById(UUID uuid) throws Exception {
        try {
            // Check if user exists
            User user = accountRepository.findUserById(uuid)
                    .orElseThrow(() -> new NotFoundException("User does not exist for given user id: [" + uuid + "]."));

            // Check if UserXP exists
            UserXP userXP = xpRepository.findById(user.getId())
                    .orElseThrow(() -> new UserXPNotFoundException("UserXP does not exist for given userId: [" + user.getId() + "]."));

            return new UserXPWithUser(user, userXP);
        }
        catch (NotFoundException e) {
            log.error("User is not found for id {}", uuid);
            throw e;
        }
        catch (UserXPNotFoundException e1) {
            log.error("UserXP is not found for id {}", uuid);
            throw new NotFoundException("UserXP does not exist for given userId: [" + uuid + "].");
        }
    }

  @Override
    public Long getUserGlobalRank(User user) throws Exception {
        try {
            return xpRepository.findGlobalUserRankByEmail(user.getEmail())
            .orElseThrow(() -> new NotFoundException("User's global XP ranking", true)).getRanking();
        } catch (NotFoundException e) {
            log.error("User {} global ranking is not found", user.getId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error in retrieving user {} global ranking", user.getId(), e);
            throw e;
        }
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

