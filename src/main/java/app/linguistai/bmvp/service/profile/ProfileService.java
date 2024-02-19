package app.linguistai.bmvp.service.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.repository.IUserHobbyRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import app.linguistai.bmvp.consts.EnglishLevels;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.profile.Hobby;
import app.linguistai.bmvp.model.profile.UserHobby;
import app.linguistai.bmvp.model.profile.UserProfile;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.IProfileRepository;
import app.linguistai.bmvp.request.QUserProfile;
import app.linguistai.bmvp.response.RUserProfile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final IProfileRepository profileRepository;
    private final IUserHobbyRepository userHobbyRepository;
    private final IAccountRepository accountRepository;
    private final HobbyService hobbyService;

    // this method should be called only when a new user is created
    public boolean createEmptyProfile(UUID userId) throws Exception {
        try {
            UserProfile dbProfile = profileRepository.findById(userId).orElse(null);

            if (dbProfile == null) {
                throw new Exception("User profile already exists!");
            }

            // save empty profile to the db
            profileRepository.save(new UserProfile());

            return true;
        } catch (Exception e) {
            System.out.println("Something is wrong in create empty profile");
            throw e;
        }
    }

    @Transactional
    public RUserProfile updateUserProfile(String email, QUserProfile profile) throws Exception { // TODO change return
        try {
            User dbUser = accountRepository.findUserByEmail(email).orElse(null);

            if (dbUser == null) {
                throw new Exception("User does not exist");
            }

            UserProfile dbProfile = profileRepository.findById(dbUser.getId()).orElse(null);

            // update user profile
            if (dbProfile == null) {
                dbProfile = profileRepository.save(
                UserProfile.builder()
                    .user(dbUser)
                    .name(profile.getName())
                    .birhtDate(profile.getBirhtDate())
                    .englishLevel(profile.getEnglishLevel())
                    .build());
            } else {
                dbProfile.setName(profile.getName());
                dbProfile.setBirhtDate(profile.getBirhtDate());
                dbProfile.setEnglishLevel(profile.getEnglishLevel());

                dbProfile = profileRepository.save(dbProfile);
            }

            List<UserHobby> userHobbies = hobbyService.updateUserHobby(dbUser, profile.getHobbies());
            return new RUserProfile(dbUser.getId(), dbProfile.getName(), dbProfile.getBirhtDate(), dbProfile.getEnglishLevel(), userHobbies); // TODO update null value what happens when user removes hobby
        } catch (Exception e) {
            System.out.println("Something is wrong in update user profile");
            e.printStackTrace();
            throw e;
        }
    }

    public RUserProfile getUserProfile(String email) throws Exception {
        try {
            // check if user exists
            User dbUser = accountRepository.findUserByEmail(email).orElse(null);

            if (dbUser == null) {
                throw new Exception("User does not exist!");
            }

            // get user profile
            UserProfile dbProfile = profileRepository.findByUserEmail(email).orElse(null);

            // get user hobbies
            List<String> hobbies = userHobbyRepository.findHobbiesByUserId(dbUser.getId());

            if (dbProfile == null) {
                return new RUserProfile(dbUser.getId(), "", null, EnglishLevels.DONT_KNOW, hobbies);
            }

            return new RUserProfile(dbProfile.getUserId(), dbProfile.getName(), dbProfile.getBirhtDate(), dbProfile.getEnglishLevel(), hobbies);
        } catch (Exception e) {
            System.out.println("Something is wrong in get profile");
            e.printStackTrace();
            throw e;
        }
    }
}
