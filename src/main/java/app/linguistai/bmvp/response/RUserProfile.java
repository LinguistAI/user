package app.linguistai.bmvp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.consts.EnglishLevels;

@Data
@Builder
@AllArgsConstructor
public class RUserProfile {
    private UUID id;
    private String name;
    private LocalDate birthDate;
    private EnglishLevels englishLevel;
    private List<String> hobbies;
}
