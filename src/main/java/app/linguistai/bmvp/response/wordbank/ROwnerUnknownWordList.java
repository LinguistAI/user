package app.linguistai.bmvp.response.wordbank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class ROwnerUnknownWordList {
    private UUID listId;
    private String ownerUsername;
    private String title;
    private String description;
    private Boolean isActive;
    private Boolean isFavorite;
    private Boolean isPinned;
}
