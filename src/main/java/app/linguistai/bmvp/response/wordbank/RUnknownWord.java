package app.linguistai.bmvp.response.wordbank;

import app.linguistai.bmvp.model.enums.ConfidenceEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RUnknownWord {
    private String word;
    private ConfidenceEnum confidence;
}
