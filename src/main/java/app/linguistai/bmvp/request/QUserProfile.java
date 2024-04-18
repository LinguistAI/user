package app.linguistai.bmvp.request;

import java.time.LocalDate;
import java.util.List;

import app.linguistai.bmvp.enums.EnglishLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QUserProfile {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private EnglishLevel englishLevel;

    @NotNull
    private List<String> hobbies;
}