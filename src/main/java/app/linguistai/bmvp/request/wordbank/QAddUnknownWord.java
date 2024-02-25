package app.linguistai.bmvp.request.wordbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class QAddUnknownWord {
    @NotBlank
    private String word;

    @NotNull
    private UUID listId;

    public QAddUnknownWord(
            @JsonProperty("word") String word,
            @JsonProperty("listId") UUID listId
    ) {
        this.word = word;
        this.listId = listId;
    }
}
