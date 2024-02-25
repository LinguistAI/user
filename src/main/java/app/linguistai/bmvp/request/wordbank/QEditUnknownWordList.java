package app.linguistai.bmvp.request.wordbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class QEditUnknownWordList {
    @NotNull
    private UUID listId;

    @NotNull
    private QUnknownWordList editedList;

    public QEditUnknownWordList(
            @JsonProperty("listId") UUID listId,
            @JsonProperty("editedList") QUnknownWordList editedList
    ) {
        this.listId = listId;
        this.editedList = editedList;
    }
}
