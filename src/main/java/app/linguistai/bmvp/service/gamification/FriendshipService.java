package app.linguistai.bmvp.service.gamification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import app.linguistai.bmvp.exception.AlreadyFoundException;
import app.linguistai.bmvp.exception.FriendException;
import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.model.Friendship;
import app.linguistai.bmvp.repository.gamification.IFriendshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.enums.FriendshipStatus;
import app.linguistai.bmvp.enums.UserSearchFriendshipStatus;
import app.linguistai.bmvp.repository.IAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendshipService {

    private final IAccountRepository accountRepository;
    private final IFriendshipRepository friendshipRepository;

    private final String REQ_USER_STR = "Requested user";
    private final String REQ_FRIEND_STR = "Friendship request";

    @Transactional
    public Friendship sendFriendRequest(String user1Email, UUID user2Id) throws Exception {
        try {
            User dbUser1 = accountRepository.findUserByEmail(user1Email).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));
            User dbUser2 = accountRepository.findUserById(user2Id).orElseThrow(() -> new NotFoundException(REQ_USER_STR, true));

            if (dbUser1.getId().equals(dbUser2.getId())) {
                throw new FriendException("You cannot send a friend request to yourself");
            }

            // check if friend request exists
            Friendship friendship = friendshipRepository.findByUserPair(dbUser1.getId(), user2Id).orElse(null);

            if (friendship != null) {
                throw new AlreadyFoundException(REQ_FRIEND_STR, true);              
            }
            
            LocalDateTime now = LocalDateTime.now();

            // save friendship to the db with pending status
            friendship = friendshipRepository.save(new Friendship(dbUser1, dbUser2, now, FriendshipStatus.PENDING));

            log.info("Friend request is sent from {} to {}", dbUser1.getId(), dbUser2.getId());

            return friendship;
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("User is not found for email {}", user1Email);
            } else {
                log.error("Requested user with id {} not found.", user2Id);
            }
            throw e;
        } catch (FriendException e) {
            log.error("Friend request failed since user with email {} tried to send request to themselves", user1Email);
            throw e;
        } catch (AlreadyFoundException e) {
            if (e.getObject().equals(FriendshipStatus.PENDING.toString())) {
                log.error("Friend request failed since request already exists between user with email {} and user with id {}", user1Email, user2Id);
            } else {
                log.error("Friend request failed since friendship already exists between user with email {} and user with id {}", user1Email, user2Id);
            }
            
            throw e;
        } catch (Exception e) {
            log.error("Friend request failed between user with email {} and user with id {}", user1Email, user2Id, e);
            throw new SomethingWentWrongException();
        }
    }

    public List<User> getFriends(String userEmail) throws Exception {
        try {
            User dbUser1 = accountRepository.findUserByEmail(userEmail).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            List<Friendship> friends = friendshipRepository.findByUser1OrUser2AndStatus(dbUser1.getId(), FriendshipStatus.ACCEPTED);

            // Create user list from the friends of the logged in user
            List<User> userFriends = friends.stream()
                .map(friend -> friend.getUser1().getEmail().equals(userEmail) ? friend.getUser2() : friend.getUser1())
                .collect(Collectors.toList());

            log.info("User {} viewed their friends.", dbUser1.getId());

            return userFriends;
        } catch (NotFoundException e) {
            log.error("Get friends failed since user does not exists for email {}", userEmail);
            throw e;
        } catch (Exception e) {
            log.error("Get friends failed for email {}", userEmail, e);
            throw new SomethingWentWrongException();
        }
    }

    public List<Friendship> getFriendRequests(String userEmail) throws Exception {
        try {
            User dbUser1 = accountRepository.findUserByEmail(userEmail).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));

            List<Friendship> friends = friendshipRepository.findByUser1OrUser2AndStatus(dbUser1.getId(), FriendshipStatus.PENDING);

            log.info("User {} viewed their friend requests.", dbUser1.getId());

            return friends;
        } catch (NotFoundException e) {
            log.error("Get friend requests failed since user does not exists for email {}", userEmail);
            throw e;
        } catch (Exception e) {
            log.error("Get friend requests failed for email {}", userEmail, e);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public Friendship acceptRequest(String user2Email, UUID user1Id) throws Exception {
        try {
            // the user who sends the request is saved to the db as user1
            User dbUser1 = accountRepository.findUserById(user1Id).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));
            User dbUser2 = accountRepository.findUserByEmail(user2Email).orElseThrow(() -> new NotFoundException(REQ_USER_STR, true));

            Friendship friendship = friendshipRepository.findByUser1IdAndUser2IdAndStatus(dbUser1.getId(), dbUser2.getId(), FriendshipStatus.PENDING).orElseThrow(() -> new NotFoundException(Friendship.class.getSimpleName(), true));

            friendship.setStatus(FriendshipStatus.ACCEPTED);

            // update the date
            friendship.setDate(LocalDateTime.now());

            // save friendship to the db with pending status
            friendship = friendshipRepository.save(friendship);

            log.info("User {} accepted friend request of {}.", dbUser2.getId(), dbUser1.getId());

            return friendship;
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("Accept friend request failed since user does not exists for id {}", user1Id);
            } else if (e.getObject().equals(REQ_USER_STR)) {
                log.error("Accept friend request failed since requested user does not exists for email {}", user2Email);
            } else if (e.getObject().equals(Friendship.class.getSimpleName())) {
                log.error("Accept friend request failed since friendship request does not exist between users {} and {}", user1Id, user2Email);
            }

            throw e;
        } catch (Exception e) {
            log.error("Accept friend request failed between users {} and {}", user1Id, user2Email, e);
            throw new SomethingWentWrongException();
        }
    }

    // user can reject the request or the sender can remove their request using this method
    @Transactional
    public Friendship rejectRequest(String user2Email, UUID user1Id) throws Exception {
        try {
            // the user who sends the request is saved to the db as user1
            User dbUser1 = accountRepository.findUserById(user1Id).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));
            User dbUser2 = accountRepository.findUserByEmail(user2Email).orElseThrow(() -> new NotFoundException(REQ_USER_STR, true));

            Friendship friendship = friendshipRepository
                                    .findByUserPairAndStatus(dbUser1.getId(), dbUser2.getId(), FriendshipStatus.PENDING)
                                    .orElseThrow(() -> new NotFoundException(Friendship.class.getSimpleName(), true));

            friendshipRepository.delete(friendship);

            log.info("User {} rejected friend request of {}.", dbUser2.getId(), dbUser1.getId());

            return friendship;
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("Reject friend request failed since user does not exists for id {}", user1Id);
            } else if (e.getObject().equals(REQ_USER_STR)) {
                log.error("Reject friend request failed since requested user does not exists for email {}", user2Email);
            } else if (e.getObject().equals(Friendship.class.getSimpleName())) {
                log.error("Reject friend request failed since friendship request does not exist between users {} and {}", user1Id, user2Email);
            }

            throw e;
        } catch (Exception e) {
            log.error("Reject friend request failed between users {} and {}", user1Id, user2Email, e);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public Friendship removeFriend(String user2Email, UUID user1Id) throws Exception {
        try {
            // the user who sends the request is saved to the db as user1
            User dbUser1 = accountRepository.findUserById(user1Id).orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), true));
            User dbUser2 = accountRepository.findUserByEmail(user2Email).orElseThrow(() -> new NotFoundException(REQ_USER_STR, true));

            Friendship friendship = friendshipRepository
                                    .findByUserPairAndStatus(dbUser1.getId(), dbUser2.getId(), FriendshipStatus.ACCEPTED)
                                    .orElseThrow(() -> new NotFoundException(Friendship.class.getSimpleName(), true));

            friendshipRepository.delete(friendship);

            log.info("User {} removed their friend {}.", dbUser1.getId(), dbUser2.getId());

            return friendship;
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("Remove friend failed since user does not exists for id {}", user1Id);
            } else if (e.getObject().equals(REQ_USER_STR)) {
                log.error("Remove friend failed since requested user does not exists for email {}", user2Email);
            } else if (e.getObject().equals(Friendship.class.getSimpleName())) {
                log.error("Remove friend failed since friendship request does not exist between users {} and {}", user1Id, user2Email);
            }

            throw e;
        } catch (Exception e) {
            log.error("Remove friend failed between users {} and {}", user1Id, user2Email, e);
            throw new SomethingWentWrongException();
        }
    }

    public UserSearchFriendshipStatus getFriendshipStatus(User loggedUser, User searchUser) throws SomethingWentWrongException {
        try {
            Friendship friendship = friendshipRepository.findByUserPair(loggedUser.getId(), searchUser.getId()).orElse(null);

            if (friendship == null) {
                return UserSearchFriendshipStatus.NOT_EXIST;
            }

            if (friendship.getStatus() == FriendshipStatus.ACCEPTED) {
                return UserSearchFriendshipStatus.FRIEND;
            } 

            if (friendship.getUser1().getId().equals(loggedUser.getId())) {
                return UserSearchFriendshipStatus.REQUEST_SENT;
            }
            
            return UserSearchFriendshipStatus.REQUEST_RECEIVED;
        } catch (Exception e) {
            log.error("Get friendship status failed between users {} and {}", loggedUser.getId(), searchUser.getId(), e);
            throw new SomethingWentWrongException();
        }
    }
}
