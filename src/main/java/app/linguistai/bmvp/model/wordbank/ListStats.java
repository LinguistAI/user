package app.linguistai.bmvp.model.wordbank;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListStats {
    @NotNull
    private Long mastered;

    @NotNull
    private Long reviewing;

    @NotNull
    private Long learning;
}
