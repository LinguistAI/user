package app.linguistai.bmvp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.model.profile.Hobby;
import app.linguistai.bmvp.model.profile.UserHobby;

@Data
@Builder
@AllArgsConstructor
public class RUserProfile {
    private UUID id;
    private String name;
    private LocalDate birhtDate;
    private Integer englishLevel;
    private List<String> hobbies;
}
