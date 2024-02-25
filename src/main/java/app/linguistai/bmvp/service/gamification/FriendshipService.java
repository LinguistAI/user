package app.linguistai.bmvp.service.gamification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.model.Friendship;
import app.linguistai.bmvp.repository.gamification.IFriendshipRepository;

import org.springframework.stereotype.Service;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.FriendshipStatus;
import app.linguistai.bmvp.repository.IAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FriendshipService {

    private final IAccountRepository accountRepository;
    private final IFriendshipRepository friendshipRepository;

    public Friendship sendFriendRequest(String user1Email, UUID user2Id) throws Exception {
        try {
            User dbUser1 = accountRepository.findUserByEmail(user1Email).orElseThrow(() -> new Exception("User is not found"));
            User dbUser2 = accountRepository.findUserById(user2Id).orElseThrow(() -> new Exception("Requested user is not found"));

            if (dbUser1.getId().equals(dbUser2.getId())) {
                throw new Exception("User cannot send friend request to themselves");
            }

            // check if friend request exists
            Friendship friendship = friendshipRepository.findByUserPair(dbUser1.getId(), user2Id).orElse(null);

            if (friendship != null) {
                throw new Exception("Friendship or request already exist");
            }
            
            LocalDateTime now = LocalDateTime.now();

            // save friendship to the db with pending status
            friendship = friendshipRepository.save(new Friendship(dbUser1, dbUser2, now, FriendshipStatus.PENDING));

            return friendship;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw e;
        }
    }

    public List<Friendship> getFriends(String userEmail) throws Exception {
        try {
            User dbUser1 = accountRepository.findUserByEmail(userEmail).orElse(null);

            if (dbUser1 == null) {
                throw new Exception("User is not found");
            }

            List<Friendship> friends = friendshipRepository.findByUser1OrUser2AndStatus(dbUser1.getId(), FriendshipStatus.ACCEPTED);

            return friends;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw e;
        }
    }

    public List<Friendship> getFriendRequests(String userEmail) throws Exception {
        try {
            User dbUser1 = accountRepository.findUserByEmail(userEmail).orElse(null);

            if (dbUser1 == null) {
                throw new Exception("User is not found");
            }

            List<Friendship> friends = friendshipRepository.findByUser1OrUser2AndStatus(dbUser1.getId(), FriendshipStatus.PENDING);

            return friends;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw e;
        }
    }

    @Transactional
    public Friendship acceptRequest(String user2Email, UUID user1Id) throws Exception {
        try {
            // the user who sends the request is saved to the db as user1
            User dbUser1 = accountRepository.findUserById(user1Id).orElse(null);
            User dbUser2 = accountRepository.findUserByEmail(user2Email).orElse(null);

            if (dbUser1 == null) {
                throw new Exception("User is not found");
            } else if (dbUser2 == null) {
                throw new Exception("Requested user is not found");
            }

            Friendship friendship = friendshipRepository.findByUser1IdAndUser2IdAndStatus(dbUser1.getId(), dbUser2.getId(), FriendshipStatus.PENDING).orElse(null);

            if (friendship == null) {
                throw new Exception("Friendship is not found");
            }

            friendship.setStatus(FriendshipStatus.ACCEPTED);

            // update the date
            friendship.setDate(LocalDateTime.now());

            // save friendship to the db with pending status
            friendship = friendshipRepository.save(friendship);

            return friendship;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    // user can reject the request or the sender can remove their request using this method
    @Transactional
    public Friendship rejectRequest(String user2Email, UUID user1Id) throws Exception {
        try {
            // the user who sends the request is saved to the db as user1
            User dbUser1 = accountRepository.findUserById(user1Id).orElse(null);
            User dbUser2 = accountRepository.findUserByEmail(user2Email).orElse(null);

            if (dbUser1 == null) {
                throw new Exception("User is not found");
            } else if (dbUser2 == null) {
                throw new Exception("Requested user is not found");
            }

            Friendship friendship = friendshipRepository.findByUserPairAndStatus(dbUser1.getId(), dbUser2.getId(), FriendshipStatus.PENDING).orElse(null);

            if (friendship == null) {
                throw new Exception("Friendship is not found");
            }

            friendshipRepository.delete(friendship);

            return friendship;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw e;
        }
    }

    @Transactional
    public Friendship removeFriend(String user2Email, UUID user1Id) throws Exception {
        try {
            // the user who sends the request is saved to the db as user1
            User dbUser1 = accountRepository.findUserById(user1Id).orElse(null);
            User dbUser2 = accountRepository.findUserByEmail(user2Email).orElse(null);

            if (dbUser1 == null) {
                throw new Exception("User is not found");
            } else if (dbUser2 == null) {
                throw new Exception("Requested user is not found");
            }

            Friendship friendship = friendshipRepository.findByUserPairAndStatus(dbUser1.getId(), dbUser2.getId(), FriendshipStatus.ACCEPTED).orElse(null);

            if (friendship == null) {
                throw new Exception("Friendship is not found");
            }

            friendshipRepository.delete(friendship);

            return friendship;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw e;
        }
    }
}
