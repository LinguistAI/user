package app.linguistai.bmvp.service.profile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.ResetToken;
import app.linguistai.bmvp.repository.IResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.UserProfile;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.IProfileRepository;
import app.linguistai.bmvp.request.QChangePassword;
import app.linguistai.bmvp.request.QUserLogin;
import app.linguistai.bmvp.request.QUserProfile;
import app.linguistai.bmvp.response.RLoginUser;
import app.linguistai.bmvp.response.RRefreshToken;
import app.linguistai.bmvp.security.JWTUserService;
import app.linguistai.bmvp.security.JWTUtils;
import app.linguistai.bmvp.service.gamification.UserStreakService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final IProfileRepository profileRepository;

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

    public UserProfile updateUserProfile(QUserProfile profile) {
        try {
            UserProfile dbProfile = profileRepository.findById(profile.getId()).orElse(null);

            if (dbProfile == null) {
                throw new Exception("User profile does not exist");
            }

            // update user profile
            dbProfile = profileRepository.updateUserProfile(profile.getId(), profile.getName(), profile.getBirhtDate(), profile.getEnglishLevel());



            return true;
        } catch (Exception e) {
            System.out.println("Something is wrong in create empty profile");
            throw e;
        }
    }
}
