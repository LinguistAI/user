package app.linguistai.bmvp.request.wordbank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class QPredefinedWordList {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Boolean isFavorite;

    @NotNull
    private Boolean isPinned;

    @NotBlank
    private String imageUrl;

    @NotNull
    private List<String> words;
}
