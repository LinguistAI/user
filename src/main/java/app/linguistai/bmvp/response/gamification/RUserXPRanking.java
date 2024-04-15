package app.linguistai.bmvp.response.gamification;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.UserXP;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RUserXPRanking {
    private User user;
    private Long experience;
    private Long ranking;
}
