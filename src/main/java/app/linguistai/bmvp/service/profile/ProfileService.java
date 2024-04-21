package app.linguistai.bmvp.service.profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import app.linguistai.bmvp.consts.Header;
import app.linguistai.bmvp.consts.ServiceUris;
import app.linguistai.bmvp.repository.IUserHobbyRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import app.linguistai.bmvp.exception.AlreadyFoundException;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.enums.EnglishLevel;
import app.linguistai.bmvp.model.profile.UserProfile;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.IProfileRepository;
import app.linguistai.bmvp.request.QUserProfile;
import app.linguistai.bmvp.response.RUserProfile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService {
    private final IProfileRepository profileRepository;
    private final IUserHobbyRepository userHobbyRepository;
    private final IAccountRepository accountRepository;
    private final HobbyService hobbyService;
    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    @Value("${ml.service.base.url}")
    private String ML_SERVICE_BASE_URL;

    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.baseUrl(ML_SERVICE_BASE_URL).build();
        }
        return webClient;
    }

    // this method should be called only when a new user is created
    public boolean createEmptyProfile(UUID userId) throws Exception {
        try {
            profileRepository.findById(userId).orElseThrow(() -> new AlreadyFoundException(UserProfile.class.getSimpleName(), true));

            // save empty profile to the db
            profileRepository.save(new UserProfile());

            return true;
        } catch (AlreadyFoundException e) {
            log.error("Create empty profile failed since profile already exists for id {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Create empty profile failed for id {}", userId, e);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public RUserProfile updateUserProfile(String email, QUserProfile profile) throws Exception {
        try {
            User dbUser = accountRepository.findUserByEmail(email)
                    .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            UserProfile dbProfile = profileRepository.findById(dbUser.getId()).orElse(null);

            // If the user profile doesn't exist, create a new one
            if (dbProfile == null) {
                dbProfile = UserProfile.builder()
                        .user(dbUser)
                        .build();
            }

            // Update fields from the profile if they are not null
            if (profile.getName() != null && !profile.getName().isEmpty()) {
                dbProfile.setName(profile.getName());
            }
            if (profile.getBirthDate() != null) {
                dbProfile.setBirthDate(profile.getBirthDate());
            }

            if (profile.getEnglishLevel() != null) {
                EnglishLevel englishLevel = EnglishLevel.fromStringOrDefault(profile.getEnglishLevel().toUpperCase());
                dbProfile.setEnglishLevel(englishLevel);
            }

            // Save the updated profile
            dbProfile = profileRepository.save(dbProfile);

            // Update user hobbies
            List<String> userHobbies = hobbyService.updateUserHobby(dbUser, profile.getHobbies());

            // Build the response profile
            RUserProfile userProfile = RUserProfile.builder()
                  .id(dbUser.getId())
                  .name(dbProfile.getName())
                  .birthDate(dbProfile.getBirthDate())
                  .englishLevel(dbProfile.getEnglishLevel())
                  .hobbies(userHobbies)
                  .build();

            // If ML service base URL is not set, return the profile without sending it to ML service
            if (ML_SERVICE_BASE_URL == null || ML_SERVICE_BASE_URL.isEmpty()) {
                log.error("ML Service Base URL is not set!");
                return userProfile;
            }

            // Create request body for the request to ML service
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("profile", userProfile);

            // Asynchronously send the updated profile to ML service
            this.getWebClient().put()
                .uri(ServiceUris.ML_SERVICE_UPDATE_PROFILE)
                .header(Header.USER_EMAIL, email)
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> {
                    if (response != null) {
                        log.info("updateProfile - Response from ML service: " + response);
                    }
                }, error -> {
                    if (error != null) {
                        log.error("updateProfile - Error from ML service: " + error.getMessage());
                    }
                });

            log.info("User {} updated their profile.", dbUser.getId());

            return userProfile;
        } catch (NotFoundException e) {
            log.error("Update profile failed since user does not exist for email {}", email);
            throw e;
        } catch (Exception e) {
            log.error("Update profile failed for email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }

    public RUserProfile getUserProfile(String email) throws Exception {
        try {
            // check if user exists
            User dbUser = accountRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            // get user profile
            UserProfile dbProfile = profileRepository.findByUserEmail(email).orElse(null);

            // get user hobbies
            List<String> hobbies = userHobbyRepository.findHobbiesByUserId(dbUser.getId());

            if (dbProfile == null) {
                return new RUserProfile(dbUser.getId(), "", null, EnglishLevel.DONT_KNOW, hobbies);
            }

            log.info("User {} viewed their profile.", dbUser.getId());

            return new RUserProfile(dbProfile.getUserId(), dbProfile.getName(), dbProfile.getBirthDate(), dbProfile.getEnglishLevel(), hobbies);
        } catch (NotFoundException e) {
            log.error("Get profile failed since user does not exist for email {}", email);
            throw e;
        } catch (Exception e) {
            log.error("Get profile failed for email {}", email, e);
            throw new SomethingWentWrongException();
        }
    }
}
