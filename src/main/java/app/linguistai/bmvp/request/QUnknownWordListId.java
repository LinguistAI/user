package app.linguistai.bmvp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class QUnknownWordListId {
    @NotNull
    private UUID listId;

    public QUnknownWordListId(
            @JsonProperty("listId") UUID listId
    ) {
        this.listId = listId;
    }
}
