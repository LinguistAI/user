package app.linguistai.bmvp.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class QUserProfile {

    private String name;

    private LocalDate birthDate;

    private String englishLevel;

    private List<String> hobbies;
}