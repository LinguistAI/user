package app.linguistai.bmvp.repository.gamification;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import app.linguistai.bmvp.model.Friendship;
import app.linguistai.bmvp.model.embedded.FriendshipId;
import app.linguistai.bmvp.enums.FriendshipStatus;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IFriendshipRepository extends JpaRepository<Friendship, FriendshipId> {

	@Query("SELECT f FROM Friendship f " +
		"WHERE (f.user1.id = :userId OR f.user2.id = :userId) " +
		"AND f.status = :status")
	List<Friendship> findByUser1OrUser2AndStatus(UUID userId, FriendshipStatus status);

	@Query("SELECT f FROM Friendship f " +
		"WHERE ((f.user1.id = :user1Id AND f.user2.id = :user2Id) " +
		"OR (f.user1.id = :user2Id AND f.user2.id = :user1Id)) " +
		"AND f.status = :status")
	Optional<Friendship> findByUserPairAndStatus(UUID user1Id, UUID user2Id, FriendshipStatus status);

	@Query("SELECT f FROM Friendship f " +
		"WHERE (f.user1.id = :user1Id AND f.user2.id = :user2Id) " +
		"OR (f.user1.id = :user2Id AND f.user2.id = :user1Id)")
	Optional<Friendship> findByUserPair(UUID user1Id, UUID user2Id);

	Optional<Friendship> findByUser1IdAndUser2IdAndStatus(UUID id1, UUID id2, FriendshipStatus status);

	@Modifying
	@Transactional
	@Query("DELETE FROM Friendship f WHERE (f.user1.id = :userId OR f.user2.id = :userId)")
	void deleteByUserId(UUID userId);
}
