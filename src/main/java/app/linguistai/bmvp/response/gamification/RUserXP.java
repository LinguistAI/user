package app.linguistai.bmvp.response.gamification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RUserXP {
    private String username;
    private Long currentExperience;
    private Long totalExperienceToNextLevel;
    private Long level;
}
