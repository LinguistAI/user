package app.linguistai.bmvp.repository.gamification.store;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.embedded.UserItemId;
import app.linguistai.bmvp.model.gamification.store.StoreItem;
import app.linguistai.bmvp.model.gamification.store.UserItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserItemRepository extends JpaRepository<UserItem, UserItemId> {
    Page<UserItem> findByUser(User user, Pageable page);
    void deleteAllByUser(User user);
    Optional<UserItem> findByUserAndStoreItem(User user, StoreItem storeItem);
}
