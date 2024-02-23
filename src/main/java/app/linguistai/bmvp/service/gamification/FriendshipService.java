package app.linguistai.bmvp.service.gamification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.model.Friendship;
import app.linguistai.bmvp.repository.gamification.IFriendshipRepository;

import org.springframework.stereotype.Service;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.embedded.FriendshipId;
import app.linguistai.bmvp.model.enums.FrinedshipStatus;
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
            User dbUser1 = accountRepository.findUserByEmail(user1Email).orElse(null);
            User dbUser2 = accountRepository.findUserById(user2Id).orElse(null);

            if (dbUser1 == null) {
                throw new Exception("User is not found");
            } else if (dbUser2 == null) {
                throw new Exception("Requested user is not found");
            }
            
            LocalDateTime now = LocalDateTime.now();

            // save friendship to the db with pending status
            Friendship friendship = friendshipRepository.save(new Friendship(dbUser1, dbUser2, now, FrinedshipStatus.PENDING));

            return friendship;
        } catch (Exception e) { // TODO check what happens if same friendship request is send, or other person sends request
            System.out.println("Friendship exception");
            throw e;
        }
    }

    public List<Friendship> getFriends(String userEmail) throws Exception {
        try {
            User dbUser1 = accountRepository.findUserByEmail(userEmail).orElse(null);

            if (dbUser1 == null) {
                throw new Exception("User is not found");
            }

            List<Friendship> friends = friendshipRepository.findByUser1IdOrUser2Id(dbUser1.getId(), dbUser1.getId());

            return friends;
        } catch (Exception e) {
            System.out.println("Friendship exception");
            throw e;
        }
    }

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

            Friendship friendship = friendshipRepository.findById(new FriendshipId(dbUser1, dbUser2)).orElse(null);

            if (friendship == null) {
                throw new Exception("Frinedship is not found");
            }

            friendship.setStatus(FrinedshipStatus.ACCEPTED);

            // update the date
            friendship.setDate(LocalDateTime.now());

            // save friendship to the db with pending status
            friendship = friendshipRepository.save(friendship);

            return friendship;
        } catch (Exception e) {
            System.out.println("Friendship exception");
            throw e;
        }
    }

    // the same method is called when user rejects the request or removes the friend
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

            Friendship friendship = friendshipRepository.findById(new FriendshipId(dbUser1, dbUser2)).orElse(null);

            if (friendship == null) {
                throw new Exception("Frinedship is not found");
            }

            friendshipRepository.delete(friendship);

            return friendship;
        } catch (Exception e) {
            System.out.println("Friendship exception");
            throw e;
        }
    }
}
