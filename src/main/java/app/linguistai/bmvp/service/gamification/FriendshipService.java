package app.linguistai.bmvp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.Friendship;
import app.linguistai.bmvp.model.ResetToken;
import app.linguistai.bmvp.repository.IResetTokenRepository;
import app.linguistai.bmvp.repository.gamification.IFriendshipRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.enums.FrinedshipStatus;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.request.QChangePassword;
import app.linguistai.bmvp.request.QUserLogin;
import app.linguistai.bmvp.response.RLoginUser;
import app.linguistai.bmvp.response.RRefreshToken;
import app.linguistai.bmvp.security.JWTUserService;
import app.linguistai.bmvp.security.JWTUtils;
import app.linguistai.bmvp.service.gamification.UserStreakService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FriendshipService {

    private final IAccountRepository accountRepository;
    private final IFriendshipRepository friendshipRepository;

    public Friendship sendFriendRequest(String user1Email, UUID user2Id) {
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

    public List<Friendship> getFriends(String userEmail) {
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
}
