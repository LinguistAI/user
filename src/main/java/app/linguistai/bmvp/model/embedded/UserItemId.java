package app.linguistai.bmvp.model.embedded;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.store.StoreItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserItemId implements Serializable {
    private User user;
    private StoreItem storeItem;
}
