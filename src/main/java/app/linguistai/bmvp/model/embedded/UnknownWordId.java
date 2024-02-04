package app.linguistai.bmvp.model.embedded;

import app.linguistai.bmvp.model.wordbank.UnknownWordList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnknownWordId implements Serializable {
    private UnknownWordList ownerList;
    private String word;
}
