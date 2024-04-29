package app.linguistai.bmvp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.enums.EnglishLevel;
import app.linguistai.bmvp.enums.UserSearchFriendshipStatus;
import app.linguistai.bmvp.model.gamification.UserStreak;
import app.linguistai.bmvp.model.profile.UserProfile;
import app.linguistai.bmvp.response.gamification.RUserXP;

@Data
@Builder
@AllArgsConstructor
public class RFriendProfile {
    private UUID id;
    private String username;
    private LocalDate birthDate;
    private EnglishLevel englishLevel;
    private List<String> hobbies;
    private Integer currentStreak;
    private RUserXP xp;
    private Long globalRank;
    private UserSearchFriendshipStatus friendshipStatus;

    public RFriendProfile(UUID friendId, String username, UserProfile profile, List<String> hobbies, UserStreak streak, RUserXP xp, Long rank, UserSearchFriendshipStatus status) {
        this.id = friendId;
        this.username = username;

        if (profile == null) {
            this.birthDate = null;
            this.englishLevel = null;
        } else {
            this.birthDate = profile.getBirthDate() != null ? profile.getBirthDate() : null;
            this.englishLevel = profile.getEnglishLevel() != null ? profile.getEnglishLevel() : null;
        }   

        this.hobbies = hobbies;
        this.currentStreak = streak == null ? 0 : streak.getCurrentStreak();
        this.xp = xp;
        this.globalRank = rank;
        this.friendshipStatus = status;
    }
}
