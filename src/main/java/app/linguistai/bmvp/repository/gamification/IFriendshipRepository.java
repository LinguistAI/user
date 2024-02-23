package app.linguistai.bmvp.repository.gamification;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.linguistai.bmvp.model.Friendship;
import app.linguistai.bmvp.model.embedded.FriendshipId;

import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IFriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    List<Friendship> findAllById(FriendshipId id);
    List<Friendship> findByUser1IdOrUser2Id(UUID user1Id, UUID user2Id);
}