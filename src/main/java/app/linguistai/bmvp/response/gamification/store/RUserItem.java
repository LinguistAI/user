package app.linguistai.bmvp.response.gamification.store;

import app.linguistai.bmvp.model.gamification.store.UserItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RUserItem {
    private UserItem userItem;
}
