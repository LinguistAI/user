package app.linguistai.bmvp.response.gamification.store;

import app.linguistai.bmvp.model.gamification.store.StoreItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RStoreItem {
    private StoreItem storeItem;
}
