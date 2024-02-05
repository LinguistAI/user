package app.linguistai.bmvp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class QCreateUnknownWordList {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Boolean isFavorite;

    public QCreateUnknownWordList(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("isActive") Boolean isActive,
            @JsonProperty("isFavorite") Boolean isFavorite
    ) {
        this.title = title;
        this.description = description;
        this.isActive = isActive;
        this.isFavorite = isFavorite;
    }
}
