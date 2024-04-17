package app.linguistai.bmvp.repository.gamification;

import app.linguistai.bmvp.model.gamification.IXPRanking;
import app.linguistai.bmvp.model.gamification.UserXP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IUserXPRepository extends JpaRepository<UserXP, UUID> {
    @Query("SELECT ux FROM UserXP ux " +
            "JOIN Friendship f ON (ux.user.id = f.user1.id OR ux.user.id = f.user2.id) " +
            "JOIN User u ON (u.id = ux.user.id) " +
            "WHERE (f.user1.id = :userId OR f.user2.id = :userId) " +
            "AND f.status = 1 " +
            "ORDER BY ux.experience DESC, u.username")
    Page<UserXP> findTopFriendsByExperience(UUID userId, Pageable pageable);

    @Query(value = "SELECT r.ranking, r.userIdAsByte, r.experience FROM (SELECT DENSE_RANK() OVER (ORDER BY ux.experience DESC, u.username) AS ranking, u.email AS email, ux.experience, ux.user_id as userIdAsByte " +
            "FROM user_xp ux JOIN user u ON u.id = ux.user_id) AS r WHERE r.email = :email",
            nativeQuery = true)
    Optional<IXPRanking> findGlobalUserRankByEmail(String email);

    @Query(value = "SELECT r.ranking, r.userIdAsByte, r.experience FROM (SELECT DENSE_RANK() OVER (ORDER BY user_friends.experience DESC, u.username) AS ranking, user_friends.experience, user_friends.user_id as userIdAsByte " +
            "FROM (SELECT ux.* FROM user_xp ux " +
            "JOIN friendship f ON ux.user_id = f.user2 " +
            "WHERE f.user1 = :userId AND f.status = 1 " +
            "UNION " +
            "SELECT ux.* FROM user_xp ux " +
            "JOIN friendship f ON ux.user_id = f.user1 " +
            "WHERE f.user2 = :userId AND f.status = 1 " +
            "UNION " +
            "SELECT ux.* FROM user_xp ux " +
            "WHERE ux.user_id = :userId) AS user_friends " +
            "JOIN user u ON u.id = user_friends.user_id) AS r " +
            "WHERE userIdAsByte = :userId",
            nativeQuery = true)
    Optional<IXPRanking> findFriendsUserRankById(UUID userId);

}
