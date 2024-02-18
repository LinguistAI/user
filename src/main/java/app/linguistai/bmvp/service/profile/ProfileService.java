package app.linguistai.bmvp.service.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.repository.IUserHobbyRepository;

import org.springframework.stereotype.Service;

import app.linguistai.bmvp.model.profile.Hobby;
import app.linguistai.bmvp.model.profile.UserHobby;
import app.linguistai.bmvp.model.profile.UserProfile;
import app.linguistai.bmvp.repository.IProfileRepository;
import app.linguistai.bmvp.request.QUserProfile;
import app.linguistai.bmvp.response.RUserProfile;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final IProfileRepository profileRepository;
    private final IUserHobbyRepository userHobbyRepository;

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

    public RUserProfile updateUserProfile(String email, QUserProfile profile) throws Exception { // TODO change return
        try {
            UserProfile dbProfile = profileRepository.findByUserEmail(email).orElse(null);

            if (dbProfile == null) {
                throw new Exception("User profile does not exist");
            }

            // update user profile
            dbProfile = profileRepository.save(
                UserProfile.builder()
                    .userId(dbProfile.getUserId())
                    .name(profile.getName())
                    .birhtDate(profile.getBirhtDate())
                    .englishLevel(profile.getEnglishLevel())
                    .build());

            List<UserHobby> userHobbies = new ArrayList<UserHobby>();
            for ( String h : profile.getHobbies()) {
                userHobbies.add(new UserHobby(dbProfile.getUserId(), h));
            }

            // add user hobies to the db
            userHobbyRepository.saveAll(userHobbies);

            return new RUserProfile(dbProfile.getUserId(), dbProfile.getName(), dbProfile.getBirhtDate(), dbProfile.getEnglishLevel(), null); // TODO update null value what happens when user removes hobby
        } catch (Exception e) {
            System.out.println("Something is wrong in create empty profile");
            throw e;
        }
    }

    public RUserProfile getUserProfile(String email) throws Exception {
        try {
            // get user profile
            UserProfile dbProfile = profileRepository.findByUserEmail(email).orElse(null);

            if (dbProfile == null) {
                throw new Exception("User profile does not exist!");
            }

            // get user hobbies
            List<Hobby> hobbies = userHobbyRepository.findAllHobbyByUserId(dbProfile.getUserId());

            return new RUserProfile(dbProfile.getUserId(), dbProfile.getName(), dbProfile.getBirhtDate(), dbProfile.getEnglishLevel(), hobbies);
        } catch (Exception e) {
            System.out.println("Something is wrong in get profile");
            throw e;
        }
    }
}
