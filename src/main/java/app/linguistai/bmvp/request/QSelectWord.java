package app.linguistai.bmvp.request;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QSelectWord {
    @NotNull
    private UUID conversationId;

    @NotNull
    @Min(value = 1, message = "Size value must be at least 1")
    private Integer size = 5;

    @NotNull
    private List<String> preservedWords;
}