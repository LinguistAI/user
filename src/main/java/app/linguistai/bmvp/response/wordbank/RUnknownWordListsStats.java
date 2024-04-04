package app.linguistai.bmvp.response.wordbank;

import app.linguistai.bmvp.model.wordbank.ListStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RUnknownWordListsStats {
    private ListStats listStats;
}
