package app.linguistai.bmvp.repository.gamification.store;

import app.linguistai.bmvp.model.gamification.store.StoreItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IStoreItemRepository extends JpaRepository<StoreItem, UUID> {
    Page<StoreItem> findByEnabled(boolean enabled, Pageable pageable);
    Optional<StoreItem> findByType(String type);
    Page<StoreItem> findByTypeIn(String[] types, Pageable pageable);
}
