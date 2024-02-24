package app.linguistai.bmvp.repository.gamification;

import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import app.linguistai.bmvp.model.Friendship;
import app.linguistai.bmvp.model.embedded.FriendshipId;

@Repository
public interface IFriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    List<Friendship> findAllById(FriendshipId id);
    
    
    @Query("SELECT f FROM Friendship f " +
       "WHERE (f.user1.id = :userId OR f.user2.id = :userId) " +
       "AND f.status = :status")
    List<Friendship> findByUser1OrUser2AndStatus(UUID userId, int status);
}