package app.linguistai.bmvp.response.wordbank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class RUnknownWordListWords {
    private ROwnerUnknownWordList unknownWordList;
    private List<RUnknownWord> words;
}
