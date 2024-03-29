package app.linguistai.bmvp.response.wordbank;

import app.linguistai.bmvp.model.wordbank.ListStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class RUnknownWordList {
    private UUID listId;
    private String title;
    private String description;
    private Boolean isActive;
    private Boolean isFavorite;
    private Boolean isPinned;
    private String imageUrl;
    private ListStats listStats;
}
