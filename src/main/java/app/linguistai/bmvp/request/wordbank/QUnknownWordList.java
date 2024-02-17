package app.linguistai.bmvp.request.wordbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QUnknownWordList {
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

    public QUnknownWordList(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("isActive") Boolean isActive,
            @JsonProperty("isFavorite") Boolean isFavorite,
            @JsonProperty("isPinned") Boolean isPinned
    ) {
        this.title = title;
        this.description = description;
        this.isActive = isActive;
        this.isFavorite = isFavorite;
        this.isPinned = isPinned;
    }
}
