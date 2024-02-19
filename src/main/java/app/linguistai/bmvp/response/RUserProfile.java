package app.linguistai.bmvp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.model.profile.Hobby;
import app.linguistai.bmvp.model.profile.UserHobby;

@Data
@AllArgsConstructor
public class RUserProfile {
    private UUID id;
    private String name;
    private LocalDate birhtDate;
    private Integer englishLevel;
    private Object hobbies;
    // private List<UserHobby> hobbies;
}
