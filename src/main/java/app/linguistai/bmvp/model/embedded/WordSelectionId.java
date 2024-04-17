package app.linguistai.bmvp.model.embedded;

import app.linguistai.bmvp.model.wordbank.UnknownWord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordSelectionId implements Serializable {
    private UUID conversationId;
    private UnknownWord word;
}
