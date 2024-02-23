package app.linguistai.bmvp.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QUserProfile {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate birhtDate;

    @NotNull
    private Integer englishLevel;

    @NotNull
    private List<String> hobbies;
}