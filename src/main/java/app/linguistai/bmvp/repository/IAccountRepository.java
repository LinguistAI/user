package app.linguistai.bmvp.repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.linguistai.bmvp.model.User;
import org.springframework.transaction.annotation.Transactional;

// @Qualifier("accountjpa")
@Repository
public interface IAccountRepository extends JpaRepository<User, UUID> {
    List<User> findAllById(UUID id);
    Optional<User> findUserById(UUID id);
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    Page<User> findByUsernameStartingWithAndEmailNot(String username, String loggedInUserEmail, Pageable pageable);

    @Query("SELECT u, " +
            "CASE " +
            "   WHEN f.status = 1 THEN 0 " +
            "   WHEN f.status = 0 AND f.user1.id = :loggedInUserId THEN 1 " +
            "   WHEN f.status = 0 AND f.user1.id = u.id THEN 2 " +
            "   ELSE 3 " +
            "END AS friendship_status " +
            "FROM User u " +
            "LEFT JOIN Friendship f ON (u.id = f.user1.id OR u.id = f.user2.id) " +
            "AND (f.user1.id = :loggedInUserId OR f.user2.id = :loggedInUserId) " +
            "WHERE u.username LIKE :usernamePrefix% " +
            "AND u.id != :loggedInUserId")
    Page<Object[]> findByUsernameStartingWithAndWithFriendshipStatusAndEmailNot(
            String usernamePrefix,
            UUID loggedInUserId,
            Pageable pageable
    );

    @Modifying
    @Transactional
    @Query("update User u set u.password = :password where u.id = :id")
    int updatePassword(@Param("password") String newPassword, @Param("id") UUID id);
}