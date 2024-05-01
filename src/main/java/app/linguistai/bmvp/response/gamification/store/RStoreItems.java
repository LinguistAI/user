package app.linguistai.bmvp.response.gamification.store;

import app.linguistai.bmvp.model.gamification.store.StoreItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class RStoreItems {
    private List<RStoreItem> storeItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
